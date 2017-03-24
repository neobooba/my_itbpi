package com.shinhan.dictionaryexam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IC-INTPC-087103 on 2017-03-24.
 */

public class Dictionary extends SQLiteOpenHelper{
    // super(~~) 에 넣을 상수 정의
    public static final String DATABASE_NAME="dictionary.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME="voca";

    public Dictionary(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //DB 파일이 존재하지 않을때 최초생성
        String query = "create table "+TABLE_NAME+" (_id integer PRIMARY KEY autoincrement, "+
                "word text, definition text)"; //테이블 생성 쿼리문
        try{ db.execSQL(query);}  //쿼리 실행
        catch (Exception e) { e.printStackTrace();}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //DB파일 버전이 변경되었을때
        //DB파일 버전이 변경되었을때 기존 테이블 삭제
        try {db.execSQL("drop table if exists "+TABLE_NAME); }
        catch (Exception e) {e.printStackTrace();}
        onCreate(db);  //DB파일 생성
    }
}
