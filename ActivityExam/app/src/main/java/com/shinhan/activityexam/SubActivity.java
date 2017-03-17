package com.shinhan.activityexam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent intent = getIntent();
        String string = intent.getStringExtra("String");
        //Toast.makeText(SubActivity.this, string, Toast.LENGTH_LONG).show();
        EditText edittext = (EditText)findViewById(R.id.edittext2);
        edittext.setText(string);
    }

    public void onCloseButtonClicked(View View){
        Intent intent = new Intent();
        EditText edittext2 = (EditText)findViewById(R.id.edittext2);
        String result = edittext2.getText().toString();
        intent.putExtra("Result", result);
        setResult(RESULT_OK, intent);
        finish();
    }


}
