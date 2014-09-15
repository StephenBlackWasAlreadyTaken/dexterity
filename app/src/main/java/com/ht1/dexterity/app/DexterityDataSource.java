package com.ht1.dexterity.app;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
/**
 * Created by John Costik on 6/8/14.
 */
public class DexterityDataSource {

    private SQLiteDatabase database;
    private DexteritySqlLiteHelper dbHelper;
    private static final String TABLE_SENSOR = "Sensor";
    private String[] allColumns = { DexteritySqlLiteHelper.COLUMN_ID,
            DexteritySqlLiteHelper.COLUMN_TRANSMITTER_ID,
            DexteritySqlLiteHelper.COLUMN_RAW_VALUE,
            DexteritySqlLiteHelper.COLUMN_FILTERED_VALUE,
            DexteritySqlLiteHelper.COLUMN_BATTERY_LIFE_VALUE,
            DexteritySqlLiteHelper.COLUMN_SIGNAL_STRENGTH_VALUE,
            DexteritySqlLiteHelper.COLUMN_UPLOADER_BATTERY_LIFE_VALUE,
            DexteritySqlLiteHelper.COLUMN_CAPTURE_DATETIME,
            DexteritySqlLiteHelper.COLUMN_UPLOADED,
            DexteritySqlLiteHelper.COLUMN_TRANSMISSION_ID};
    private Context mContext;

    public DexterityDataSource(Context context) {

        dbHelper = new DexteritySqlLiteHelper(context);
        database = dbHelper.getWritableDatabase();
        mContext = context;

    }

    public TransmitterRawData createRawDataEntry (TransmitterRawData data) {
        ContentValues values = new ContentValues();
        values.put(DexteritySqlLiteHelper.COLUMN_TRANSMITTER_ID, data.getTransmitterId());
        values.put(DexteritySqlLiteHelper.COLUMN_RAW_VALUE, data.getRawValue());
        values.put(DexteritySqlLiteHelper.COLUMN_FILTERED_VALUE, data.getFilteredValue());
        values.put(DexteritySqlLiteHelper.COLUMN_BATTERY_LIFE_VALUE, data.getBatteryLife());
        values.put(DexteritySqlLiteHelper.COLUMN_SIGNAL_STRENGTH_VALUE, data.getReceivedSignalStrength());
        values.put(DexteritySqlLiteHelper.COLUMN_UPLOADER_BATTERY_LIFE_VALUE, data.getUploaderBatteryLife());
        values.put(DexteritySqlLiteHelper.COLUMN_CAPTURE_DATETIME, data.getCaptureDateTime());
        values.put(DexteritySqlLiteHelper.COLUMN_UPLOADED, data.getUploaded());
        values.put(DexteritySqlLiteHelper.COLUMN_TRANSMISSION_ID, data.getTransmissionId());

        long insertId = database.insert(DexteritySqlLiteHelper.TABLE_SENSOR, null,
                values);
        Cursor cursor = database.query(DexteritySqlLiteHelper.TABLE_SENSOR,
                allColumns, DexteritySqlLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        TransmitterRawData newRawData = cursorToRawData(cursor);
        cursor.close();
        mContext.sendBroadcast(new Intent("NEW_READ"));
        return newRawData;
    }

    public void updateRawDataEntry (TransmitterRawData data) {

        String strFilter = "_id=" + data.get_id();
        ContentValues args = new ContentValues();
        args.put(DexteritySqlLiteHelper.COLUMN_UPLOADED, data.getUploaded());
        database.update(DexteritySqlLiteHelper.TABLE_SENSOR, args, strFilter, null);
    }

    public void deleteSensorData(TransmitterRawData data) {
        long id = data.get_id();
        System.out.println("Data deleted with id: " + id);
        database.delete(DexteritySqlLiteHelper.TABLE_SENSOR, DexteritySqlLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<String> getAllData() {
        List<String> dataList = new ArrayList<String>();
        String orderBy =  DexteritySqlLiteHelper.COLUMN_ID + " DESC";
        Cursor cursor = database.query(DexteritySqlLiteHelper.TABLE_SENSOR,
                allColumns, null, null, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TransmitterRawData data = cursorToRawData(cursor);
            dataList.add(data.toTableString());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return dataList;
    }

    public List<TransmitterRawData> getAllDataToUploadObjects() {
        List<TransmitterRawData> dataList = new ArrayList<TransmitterRawData>();
        String orderBy =  DexteritySqlLiteHelper.COLUMN_ID + " DESC";
        Cursor cursor = database.query(DexteritySqlLiteHelper.TABLE_SENSOR,
                allColumns, "Uploaded=0", null, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TransmitterRawData data = cursorToRawData(cursor);
            dataList.add(data);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return dataList;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private TransmitterRawData cursorToRawData(Cursor cursor) {
        TransmitterRawData rawData = new TransmitterRawData();

        rawData.set_id(cursor.getLong(0));
        rawData.setTransmitterId(cursor.getString(1));
        rawData.setRawValue(cursor.getInt(2));
        rawData.setFilteredValue(cursor.getInt(3));
        rawData.setBatteryLife(cursor.getInt(4));
        rawData.setReceivedSignalStrength(cursor.getInt(5));
        rawData.setUploaderBatteryLife(cursor.getInt((6)));

        rawData.setCaptureDateTime(cursor.getLong((7)));
        rawData.setUploaded(cursor.getInt(8));
        rawData.setTransmissionId(cursor.getInt(9));

        rawData.setUploadAttempts(0);

        return rawData;
    }
}
