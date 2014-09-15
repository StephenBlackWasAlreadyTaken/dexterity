package com.ht1.dexterity.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by John Costik on 6/8/14.
 */
public class DexteritySqlLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_SENSOR = "Sensor";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRANSMITTER_ID = "Transmitter_id";
    public static final String COLUMN_RAW_VALUE = "Raw_value";
    public static final String COLUMN_FILTERED_VALUE = "Filtered_value";
    public static final String COLUMN_BATTERY_LIFE_VALUE = "Battery_life_value";
    public static final String COLUMN_SIGNAL_STRENGTH_VALUE = "RSSI_value";
    public static final String COLUMN_UPLOADER_BATTERY_LIFE_VALUE = "Uploader_battery_life_value";
    public static final String COLUMN_CAPTURE_DATETIME = "Captured_DateTime";
    public static final String COLUMN_UPLOADED = "Uploaded";
    public static final String COLUMN_TRANSMISSION_ID = "Transmission_id";


    private static final String DATABASE_NAME = "Sensor.db";
    private static final int DATABASE_VERSION = 4;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SENSOR + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TRANSMITTER_ID + " text, "
            +  COLUMN_RAW_VALUE + " integer, "
            + COLUMN_FILTERED_VALUE + " integer, "
            + COLUMN_BATTERY_LIFE_VALUE + " integer, "
            + COLUMN_SIGNAL_STRENGTH_VALUE + " integer, "
            + COLUMN_UPLOADER_BATTERY_LIFE_VALUE + " integer, "
            + COLUMN_UPLOADED + " integer, "
            + COLUMN_CAPTURE_DATETIME + " DATETIME, "
            + COLUMN_TRANSMISSION_ID + " integer" + ");";

    public DexteritySqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DexteritySqlLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        onCreate(db);
    }
}
