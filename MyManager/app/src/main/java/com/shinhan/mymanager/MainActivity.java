package com.shinhan.mymanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
    }

    public void onButtonClicked (View view) {
        EditText password = (EditText)findViewById(R.id.password);

        if(password.getText().toString().equals("1231")) {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),"비밀번호가 틀렸습니다"+password.getText(),Toast.LENGTH_SHORT).show();
        }
    }
}
