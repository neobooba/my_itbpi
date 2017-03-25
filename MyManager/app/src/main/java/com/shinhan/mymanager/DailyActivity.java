package com.shinhan.mymanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
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
        EditText content = (EditText)findViewById(R.id.content);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

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
                ((TextView)adapterView.getChildAt(0)).setTextColor(Color.BLACK);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();
        long dateTime = intent.getLongExtra("date", 0);
        String contents = intent.getStringExtra("content");
        String projects = intent.getStringExtra("project");
        Log.i("+++++project:" , projects);
        content.setText(contents);
        Date date = new Date(dateTime);
        dailyTitle.setText(new SimpleDateFormat("yyyy.MM.dd E").format(date));
        DailyActivity.dd = new SimpleDateFormat("yyyyMMdd").format(date);

        for (int i = 0; i < items.length; i++) {
            if (projects.equals(items[i])) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public void onListClickec (View view) {
        Intent intent = new Intent(DailyActivity.this, ListActivity.class);
        startActivity(intent);
    }

    public void onInsertClicked (View view) {
        EditText content = (EditText)findViewById(R.id.content);
        Spinner project = (Spinner)findViewById(R.id.pj_spinner);

        String contentString = content.getText().toString();
        Log.i("+++++Insert Button",contentString);
        Log.i("+++++sel pj",  project.toString());
        if (!contentString.isEmpty() ) {
            writeDatabase(contentString);
        } else {
            Toast.makeText(getApplicationContext(),"비밀번호가 틀렸습니다",Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(DailyActivity.this, ListActivity.class);
        startActivity(intent);
    }

    public void onDeleteClicked(View view) {
        String dd = DailyActivity.dd;
        Log.i("++++delete dd:", dd.toString());
        Daily daily = new Daily(DailyActivity.this); //DB파일 열기
        SQLiteDatabase database = daily.getWritableDatabase(); //쓰기모드로 열기
        int rowAffected = database.delete(Daily.TABLE_NAME,"dd="+dd, null);
        Toast.makeText(getApplicationContext(),rowAffected+"삭제되었습니다",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(DailyActivity.this, ListActivity.class);
        startActivity(intent);
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
        values.put("project",pj);
        values.put("content",content); //word 컬럼에 데이터 저장
        database.insert(Daily.TABLE_NAME, null, values); //DB에 데이터 insert
    }


}
