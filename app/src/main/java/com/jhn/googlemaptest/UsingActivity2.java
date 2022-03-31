package com.jhn.googlemaptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UsingActivity2 extends AppCompatActivity {

    TextView txt;
    TextView txt2;
    TextView txt3;
    Button btn;
    private Timer mTimer;
    String getTime;
    String dateString;
    Dialog dilaog01;
    long duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using2);

        txt = findViewById(R.id.txt);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        btn = findViewById(R.id.btn);

        String dTime = "HH:mm";
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat(dTime);
        getTime = dateFormat.format(date);
        txt.setText(getTime+"분");


        UsingActivity2.MainTimerTask timerTask = new UsingActivity2.MainTimerTask();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 500, 1000);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });




    }

    private Handler mHandler = new Handler();

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {

                Date rightNow = new Date();

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

                dateString = formatter.format(rightNow);

                Date startDate = formatter.parse(getTime);
                Date endDate = formatter.parse(dateString);
                duration=endDate.getTime()-startDate.getTime();
                txt2.setText(duration/60000+"분");




            }catch (Exception e){

            }


        }
    };

    class MainTimerTask extends TimerTask {
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }
    @Override
    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        UsingActivity2.MainTimerTask timerTask = new UsingActivity2.MainTimerTask();
        mTimer.schedule(timerTask, 500, 3000);
        super.onResume();
    }

    private void showDialog(){
        dilaog01 = new Dialog(UsingActivity2.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_finish_dialog);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));

        Button firstbtn = dilaog01.findViewById(R.id.firstbtn);
        Button lastbtn = dilaog01.findViewById(R.id.lastbtn);
        TextView txt = dilaog01.findViewById(R.id.txt);
        txt.setText("재밌게 이용하셨나요??");

        firstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선불 이용할 경우
                Intent intent = new Intent(UsingActivity2.this, ResultActivity.class);
                intent.putExtra("duration",duration);
                startActivity(intent);
            }
        });

        lastbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dilaog01.dismiss();
            }
        });

    }
    @Override
    public void onBackPressed() {
        showDialog();
    }
}


