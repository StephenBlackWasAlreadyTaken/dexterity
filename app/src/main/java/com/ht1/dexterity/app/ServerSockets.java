package com.ht1.dexterity.app;
import java.net.*;
import java.util.List;
import java.util.ListIterator;
import java.io.*;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import static java.lang.Math.*;

//import com.nightscout.android.dexcom.DataStore;
//import com.nightscout.android.dexcom.DexcomG4Activity;
//import com.nightscout.android.dexcom.EGVRecord;
// This class is a server that is listening to connections on the WIFI network and sends them data as expected.
//
// The protocol of the data should be: 1 recieve a command of the desired data.
// 2) the server sends the data and closes the connection. the connection will be serielized EGV records.
public class ServerSockets  extends Thread {
	// TODO: rename class...

    static public String mDebugString;

    private volatile boolean mStop = false;
    
    public final static int PORT = 50005 ; // will have to be configurable at last...
    
    //DexterityActivity mDexterityActivity;
    private final Context mContext;
	   
    public ServerSockets(Context ctx) throws IOException
    {
        //mDexterityActivity = da;
        mContext = ctx.getApplicationContext(); 
    }

    void PrintSocketStatus(String Str) 
    {
        mDebugString += Str;
        mContext.sendBroadcast(new Intent("NEW_PRINT"));
    }

    public void Stop()
    {
        mStop = true;
    }

    public void run()
    {
        Gson gson = new GsonBuilder().create();

        ComunicationHeader ch;

        ServerSocket ServerSocket;
        
        try {
            ServerSocket = new ServerSocket(PORT);
            ServerSocket.setSoTimeout(2000);
        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            PrintSocketStatus("cought SocketException!, exiting...");
            return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            PrintSocketStatus("cought IOException!, exiting...");
            return;
        }

        
        while(!mStop)
        {
            try
            {
                Socket clientSocket = ServerSocket.accept();
                PrintSocketStatus("got connection from " + clientSocket.getRemoteSocketAddress() );

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(	new InputStreamReader(clientSocket.getInputStream()));
                
                
    
                String inputLine, outputLine;

                inputLine = in.readLine();
                PrintSocketStatus("Recieved the line " + inputLine);
                ch = gson.fromJson(inputLine, ComunicationHeader.class);  				   
                  
                if(ch.version != 1) {
                    PrintSocketStatus("Unexpected version...\n" + ch.version);
                    clientSocket.close();
                    continue;
                }
                PrintSocketStatus("Recieved the line + correct version " + inputLine);

                // Get all the data that is currently stored
                DexterityDataSource source = new DexterityDataSource(mContext);
                PrintSocketStatus("source = " + source);
                List<TransmitterRawData> rawDataList = source.getAllDataToUploadObjects();

                PrintSocketStatus("List size is " + rawDataList.size());
                source.close();

                int objectsToReturn = min(rawDataList.size(), ch.numberOfRecords);
                for (ListIterator<TransmitterRawData> iter = rawDataList.listIterator(); objectsToReturn > 0; ) {
                    TransmitterRawData element = iter.next();
                    outputLine = gson.toJson(element);
                    out.println(outputLine);
                    objectsToReturn--;
                }
                // Mark that we have finished sending.
                out.println("");

                PrintSocketStatus("Data send, closing socket......");
                clientSocket.close();
            }catch(SocketTimeoutException s) {
                PrintSocketStatus("Socket timed out(server)!, trying again...");
            }catch(IOException e) {
                PrintSocketStatus("cought IOException!, trying again...");
            }
            catch (JsonSyntaxException je) {
                PrintSocketStatus("cought JsonSyntaxException!, trying again...");
            }
        }
    }

 }