package com.kodingen.cetrin.posttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "trackcodes";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // создаем таблицу с полями
        sqLiteDatabase.execSQL("create table trackcodes ("
                + "id integer primary key autoincrement,"
                + "trackcode text,"
                + "descr text,"
                + "statuscode text,"
                + "lastoffice text,"
                + "lastindex text,"
                + "eventdescr text,"
                + "date text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public boolean addTrackCode(BarcodeInfo info, String description) {
        ContentValues cv = new ContentValues();
        // connect to database
        SQLiteDatabase db = getWritableDatabase();
        cv.put("trackcode", info.getBarcode());
        cv.put("descr", "");
        cv.put("statuscode", info.getCode());
        cv.put("lastoffice", info.getLastOffice());
        cv.put("lastindex", info.getLastOfficeIndex());
        cv.put("eventdescr", info.getEventDescription());
        cv.put("date", info.getEventDate());
        long rowID = db.insert("trackcodes", null, cv);
        db.close();
        if (rowID == -1) {
            return false;
        }
        return true;
    }
}
