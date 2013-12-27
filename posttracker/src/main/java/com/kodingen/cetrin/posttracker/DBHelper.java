package com.kodingen.cetrin.posttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static final String COL_TRACKCODE = "trackcode";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_LASTDATE = "lastchecked";

    private static final String DB_NAME = "trackcodes";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "trackcodes";
    private SQLiteDatabase mDB;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void open() {
        mDB = getWritableDatabase();
    }

    public void close() {
        super.close();
        mDB = null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // создаем таблицу с полями
        sqLiteDatabase.execSQL("create table " + DB_TABLE + " ("
                + " _id integer primary key autoincrement,"  //0
                + COL_TRACKCODE + " text,"                   //1
                + COL_DESCRIPTION + " text,"                 //2
                + "statuscode text,"                         //3
                + "lastoffice text,"                         //4
                + "lastindex text,"                          //5
                + "eventdescr text,"                         //6
                + "date text,"                               //7
                + COL_LASTDATE + " text" + ");");            //8
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public boolean isCodeInDB(String trackCode) {
        Cursor cursor = mDB.query(DB_TABLE, null, COL_TRACKCODE + "=\"" + trackCode.toUpperCase() + "\"", null, null, null, null);
        return cursor.getCount() != 0;
    }

    public boolean addTrackCode(BarcodeInfo info) {
        if (mDB == null) {
            throw new IllegalStateException("Connection to database not open");
        }
        if (isCodeInDB(info.getBarcode())) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_TRACKCODE, info.getBarcode());
        cv.put(COL_DESCRIPTION, info.getDescription());
        cv.put("statuscode", info.getCode());
        cv.put("lastoffice", info.getLastOffice());
        cv.put("lastindex", info.getLastOfficeIndex());
        cv.put("eventdescr", info.getEventDescription());
        cv.put("date", info.getEventDate());
        Time now = new Time();
        now.setToNow();
        cv.put(COL_LASTDATE, now.format("%c"));
        long rowID = mDB.insert(DB_TABLE, null, cv);
        if (rowID == -1) {
            return false;
        }
        return true;
    }

    public BarcodeInfo getInfo(String trackcode) {
        Cursor info = mDB.query(DB_TABLE, null, COL_TRACKCODE + "=\"" + trackcode.toUpperCase() + "\"", null, null, null, null);
        if (info.getCount() == 0) {
            return null;
        }
        info.moveToFirst();
        BarcodeInfo codeInfo = new BarcodeInfo();
        codeInfo.setBarcode(info.getString(1));
        codeInfo.setDescription(info.getString(2));
        codeInfo.setCode(info.getString(3));
        codeInfo.setLastOffice(info.getString(4));
        codeInfo.setLastOfficeIndex(info.getString(5));
        codeInfo.setEventDescription(info.getString(6));
        codeInfo.setEventDate(info.getString(7));
        codeInfo.setLastCheck(info.getString(8));
        return codeInfo;
    }

    public Cursor getAllData() {
        if (mDB == null) {
            throw new IllegalStateException("Connection to database not open");
        }
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public void delRecord(long id) {
        if (mDB == null) {
            throw new IllegalStateException("Connection to database not open");
        }
        mDB.delete(DB_TABLE, "_id = " + id, null);
    }

    public int updateTrackInfo(BarcodeInfo info) {
        if (mDB == null) {
            throw new IllegalStateException("Connection to database not open");
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_TRACKCODE, info.getBarcode());
        cv.put(COL_DESCRIPTION, info.getDescription());
        cv.put("statuscode", info.getCode());
        cv.put("lastoffice", info.getLastOffice());
        cv.put("lastindex", info.getLastOfficeIndex());
        cv.put("eventdescr", info.getEventDescription());
        cv.put("date", info.getEventDate());
        cv.put(COL_LASTDATE, info.getLastCheck());
        return mDB.update(DB_TABLE, cv, COL_TRACKCODE + "=\"" + info.getBarcode() + "\"", null);
    }
}
