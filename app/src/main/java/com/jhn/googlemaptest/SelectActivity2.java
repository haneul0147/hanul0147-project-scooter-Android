package com.jhn.googlemaptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhn.googlemaptest.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SelectActivity2 extends AppCompatActivity {

    TextView txt;
    TextView txt2;
    Button btn;
    Button safecheckbtn;
    Button inforbtn;
    Dialog dilaog01;

    ProgressDialog progressDialog;

    CheckBox inforcheck;
    CheckBox checkBox;
    private Timer mTimer;
    String dateString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select2);


        txt = findViewById(R.id.txt);
        txt2 = findViewById(R.id.txt2);
        btn = findViewById(R.id.btn);
        safecheckbtn = findViewById(R.id.safecheckbtn);
        checkBox = findViewById(R.id.check1) ;
        inforcheck   = findViewById(R.id.inforcheck );
        inforbtn  = findViewById(R.id.inforbtn) ;


        MainTimerTask timerTask = new MainTimerTask();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 500, 1000);



        inforcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inforcheck.isChecked()) {
                    showDialog();
                } else {

                }
            }
        });
        inforbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    showAlertDialog();
                } else {

                }
            }
        });

        safecheckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()&& inforcheck.isChecked()){
                        showProgress("잠시만 기다려주세요....");
                        Intent intent = new Intent(SelectActivity2.this, UsingActivity2.class);
                        startActivity(intent);
                        dismissProgress();
                        finish();
                }else {
                    Toast.makeText(SelectActivity2.this,"모든 내용을 확인 후 이용가능합니다.",Toast.LENGTH_SHORT).show();
                }
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
                txt.setText(dateString+"분");

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
        MainTimerTask timerTask = new MainTimerTask();
        mTimer.schedule(timerTask, 500, 3000);
        super.onResume();
    }
    // 우리가 만든 함수. 화면에 네트워크 처리중이라고 표시할 것.
    private void showProgress(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    private void dismissProgress(){
        progressDialog.dismiss();
    }

    private void showAlertDialog() {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.safe);


        AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity2.this);
        builder.setTitle("안전수칙");
        builder.setView(image)
                .setPositiveButton("확인했어요!!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkBox.setChecked(true) ;

                    }
                });

        AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1500;
        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));
        alertDialog.show();
        alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Window window = alertDialog.getWindow();
        window.setAttributes(lp);
    }
    private void showDialog(){
        dilaog01 = new Dialog(SelectActivity2.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_informatioon_dialog);

        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));
        WindowManager.LayoutParams params = dilaog01.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dilaog01.getWindow().setAttributes(params);
        dilaog01.show();

        Button btn = dilaog01.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inforcheck.setChecked(true);
                dilaog01.dismiss();

            }
        });


    }
}


