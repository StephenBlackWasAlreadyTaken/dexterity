import java.net.*;
import java.util.List;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class ReadData {
    public static void main(String[] args) {
        int numberOfRecords = 5;
        if(args.length >= 1) {
            numberOfRecords = Integer.parseInt(args[0]);
        }
        Read("192.168.1.13", 50005, numberOfRecords);
    }


    public static boolean Read(String hostName,int port, int numberOfRecords)
    {
        try
        {

            Gson gson = new GsonBuilder().create();

            // An example of using gson.
            ComunicationHeader ch = new ComunicationHeader();
            ch.version = 1;
            ch.numberOfRecords = numberOfRecords;
            String flat = gson.toJson(ch);
            ComunicationHeader ch2 = gson.fromJson(flat, ComunicationHeader.class);  
            System.out.println("Results code" + flat + ch2.version);


            // Real client code
            Socket MySocket = new Socket(hostName, port);

            System.out.println("After the new socket \n");
            MySocket.setSoTimeout(5000); 
                     
            System.out.println("client connected... " );
            
            PrintWriter out = new PrintWriter(MySocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(MySocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            out.println(flat);
            
            while(true) {
                String data = in.readLine();
                if(data == null) {
                    System.out.println("recieved null exiting");
                    break;
                }
                if(data.equals("")) {
                    System.out.println("recieved \"\" exiting");
                    break;
                }

                System.out.println( "data size " +data.length() + " data = "+ data);
                TransmitterRawData trd = gson.fromJson(data, TransmitterRawData.class);
                System.out.println( trd.toTableString());
            }


            MySocket.close();
            return true;
        }catch(SocketTimeoutException s) {
            System.out.println("Socket timed out!...");
        }
        catch(IOException e) {
            e.printStackTrace();
            System.out.println("cought exception " + e.getMessage());
        }
        return false;
   }
}