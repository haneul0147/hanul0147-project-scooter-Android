package com.jhn.googlemaptest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.security.acl.Permission;

public class LogoActivity extends AppCompatActivity {

    Thread thread;
    boolean interrupted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);


        final Handler handler = new Handler();
        final Runnable doNextActivity = new Runnable() {
            @Override
            public void run() {

                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(LogoActivity.this, RequestActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        thread = new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(2000);
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