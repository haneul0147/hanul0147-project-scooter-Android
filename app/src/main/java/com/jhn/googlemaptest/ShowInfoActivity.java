package com.jhn.googlemaptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jhn.googlemaptest.utils.Utils;

public class ShowInfoActivity extends AppCompatActivity {

    Thread thread;
    boolean interrupted = false;
    Button btn;
    String CHECKBOX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        Intent intent = getIntent();

        btn=findViewById(R.id.btn);




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Number = intent.getExtras().getInt("Number");
                if(Number == 1){
                    Intent intent = new Intent(ShowInfoActivity.this, SelectActivity.class);
                    startActivity(intent);
                    finish();

                }if (Number == 2){
                    Intent intent = new Intent(ShowInfoActivity.this, SelectActivity2.class);
                    intent.putExtra("Count", 2);
                    startActivity(intent);
                    finish();
                }
            }
        });


        final Handler handler = new Handler();
        final Runnable doNextActivity = new Runnable() {
            @Override
            public void run() {

                int Number = intent.getExtras().getInt("Number");
                if(Number == 1){
                Intent intent1 = new Intent(ShowInfoActivity.this, SelectActivity.class);
                startActivity(intent1);
                finish();

            }else {
                    Intent intent1 = new Intent(ShowInfoActivity.this, SelectActivity2.class);
                    intent.putExtra("Count", 2);
                    startActivity(intent1);
                    finish();
                }

        }
        };

        thread = new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(5000);
                if(!interrupted){
                    handler.post(doNextActivity);
                }
            }
        };
        thread.start();
    }

    @Override
    public void onBackPressed() {
        interrupted = true;
        super.onBackPressed();
    }
}