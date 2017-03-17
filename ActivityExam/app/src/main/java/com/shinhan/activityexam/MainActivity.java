package com.shinhan.activityexam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked(View View) {
        EditText editText = (EditText)findViewById(R.id.edittext);
        String string = editText.getText().toString();
        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        intent.putExtra("String", string);
        //startActivity(intent);
        startActivityForResult(intent, 0);   //0 - SubActivity 로 지정
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {//SubActivity 가 종료되었으면
            if (resultCode == RESULT_OK) { //값을 넘기는 정상 종료일때만
                String result = data.getStringExtra("Result");
                Log.i("onActivity....",result);
                EditText editText = (EditText) findViewById(R.id.edittext);
                editText.setText(result);
            }
        }
    }

}
