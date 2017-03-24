package com.shinhan.mymanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyActivity extends AppCompatActivity {

    String[] items = {"P2017-1201-01", "P2017-1444-02", "P2017-3219-01", "P2017-4189-02"};
    public static String dd ="";
    public static String pj="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView dailyTitle = (TextView)findViewById(R.id.dailyTitle);
        Spinner spinner = (Spinner)findViewById(R.id.pj_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Log.i("++++++spinner : ",items[position]);
                DailyActivity.pj=items[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();
        long dateTime = intent.getLongExtra("date", 0);
        Date date = new Date(dateTime);
        dailyTitle.setText(new SimpleDateFormat("yyyy.MM.dd E").format(date));
        DailyActivity.dd = new SimpleDateFormat("yyyyMMdd").format(date);
    }

    public void onInsertClicked (View view) {
        EditText content = (EditText)findViewById(R.id.content);
        Spinner project = (Spinner)findViewById(R.id.pj_spinner);
        //project.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        String contentString = content.getText().toString();
        Log.i("+++++Insert Button",contentString);
        Log.i("+++++sel pj",  project.toString());
        //String ddString = dd.toString();
        if (!contentString.isEmpty() ) { //단어,뜻을 입력했으면
            writeDatabase(contentString);  //DB에 저장
            //readDatabase(); //DB 내용 읽기
        }
    }


    public void writeDatabase(String content){
        String dd = DailyActivity.dd;
        String pj = DailyActivity.pj;
        Log.i("+++++ writeDatabase","OK");
        Log.i("+++++ dd :", dd.toString());
        Log.i("+++++ pj :", pj.toString());
        Daily daily = new Daily(DailyActivity.this); //DB파일 열기
        SQLiteDatabase database = daily.getWritableDatabase(); //쓰기모드로 열기
        ContentValues values = new ContentValues(); //저장 객체 생성
        values.put("dd",dd);
        values.put("pj",pj);
        values.put("content",content); //word 컬럼에 데이터 저장
        database.insert(Daily.TABLE_NAME, null, values); //DB에 데이터 insert
    }

}
