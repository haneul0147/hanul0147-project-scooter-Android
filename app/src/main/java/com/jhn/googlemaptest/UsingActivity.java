package com.jhn.googlemaptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UsingActivity extends AppCompatActivity {

    TextView txt3;
    Button btn;
    long duration;
    Dialog dilaog01;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using);

        Intent intent = getIntent();
        duration = intent.getExtras().getLong("duration");

        txt3= findViewById(R.id.txt3);
        btn = findViewById(R.id.btn);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        CountDownTimer countDownTimer = new CountDownTimer(duration, 60000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // duration = millisUntilFinished/1000;
                txt3.setText(millisUntilFinished/60000+1+"분");
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(UsingActivity.this, ResultActivity.class);
                intent.putExtra("duration",duration);
                startActivity(intent);
            }
        };
        countDownTimer.start();
    }

    private void showDialog(){
        dilaog01 = new Dialog(UsingActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_finish_dialog);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));

        Button firstbtn = dilaog01.findViewById(R.id.firstbtn);
        Button lastbtn = dilaog01.findViewById(R.id.lastbtn);
        TextView txt = dilaog01.findViewById(R.id.txt);
        txt.setText("반납 시간이 빨라요!!");

        firstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선불 이용할 경우
                Intent intent = new Intent(UsingActivity.this, ResultActivity.class);
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
