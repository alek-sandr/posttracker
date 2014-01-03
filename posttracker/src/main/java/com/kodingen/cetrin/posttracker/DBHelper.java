package com.kodingen.cetrin.posttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

public class DBHelper extends SQLiteOpenHelper {
    public static final String COL_TRACKCODE = "trackcode";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_LASTCHECK = "lastchecked";
    public static final String COL_STATUSCODE = "statuscode";
    public static final String COL_LASTOFFICE = "lastoffice";
    public static final String COL_LASTINDEX = "lastindex";
    public static final String COL_EVENTDESCR = "eventdescr";
    public static final String COL_EVENTDATE = "eventdate";
    public static final String COL_SENDDATE = "senddate";
    public static final String COL_DAYSFORDELIVERY = "daysfordelivery";

    private static final String DB_NAME = "trackcodes";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "trackcodes";
    private SQLiteDatabase mDB;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public boolean isOpen() {
        return mDB != null;
    }

    public void open() {
        if (mDB == null) {
            mDB = getWritableDatabase();
        }
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
                + COL_STATUSCODE + " text,"                  //3
                + COL_LASTOFFICE + " text,"                  //4
                + COL_LASTINDEX + " text,"                   //5
                + COL_EVENTDESCR + " text,"                  //6
                + COL_EVENTDATE + " text,"                   //7
                + COL_LASTCHECK + " text,"                   //8
                + COL_SENDDATE + " integer,"                 //9
                + COL_DAYSFORDELIVERY + " integer);");       //10
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
        long rowID = mDB.insert(DB_TABLE, null, codeinfoToCV(info));
        if (rowID == -1) {
            return false;
        }
        return true;
    }

    public BarcodeInfo getInfo(String trackcode) {
        if (mDB == null) {
            throw new IllegalStateException("Connection to database not open");
        }
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
        codeInfo.setSendDate(info.getLong(9));
        codeInfo.setMaxDeliveryDays(info.getInt(10));
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
        return mDB.update(DB_TABLE, codeinfoToCV(info), COL_TRACKCODE + "=\"" + info.getBarcode() + "\"", null);
    }

    private ContentValues codeinfoToCV(BarcodeInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TRACKCODE, info.getBarcode());
        cv.put(COL_DESCRIPTION, info.getDescription());
        cv.put(COL_STATUSCODE, info.getCode());
        cv.put(COL_LASTOFFICE, info.getLastOffice());
        cv.put(COL_LASTINDEX, info.getLastOfficeIndex());
        cv.put(COL_EVENTDESCR, info.getEventDescription());
        cv.put(COL_EVENTDATE, info.getEventDate());
        cv.put(COL_LASTCHECK, info.getLastCheck());
        cv.put(COL_SENDDATE, info.getSendDate());
        cv.put(COL_DAYSFORDELIVERY, info.getMaxDeliveryDays());
        return cv;
    }
}
