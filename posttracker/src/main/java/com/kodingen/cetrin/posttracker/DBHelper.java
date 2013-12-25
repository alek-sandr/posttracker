package com.kodingen.cetrin.posttracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}
