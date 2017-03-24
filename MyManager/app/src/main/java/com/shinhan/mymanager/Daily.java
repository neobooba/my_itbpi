package com.shinhan.mymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IC-INTPC-087103 on 2017-03-24.
 */

public class Daily extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="daily.db";
    public static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="dailylist";

    public Daily(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table "+TABLE_NAME+" (_id integer PRIMARY KEY autoincrement, "+
                "dd text, project text, content text)";
        try { db.execSQL(query); }
        catch (Exception e) { e.printStackTrace(); }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try { db.execSQL("drop table if exists "+TABLE_NAME); }
        catch (Exception e) { e.printStackTrace(); }
        onCreate(db);
    }
}
