package com.jhn.googlemaptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SelectActivity extends AppCompatActivity {

    TextView txt2;
    TextView txt4;
    TextView txt5;
    TextView txtbtn;

    Button safecheckbtn;
    Button inforbtn;
    CheckBox checkBox;
    CheckBox inforcheck;
    long duration;
    Dialog dilaog01;

    String dMonth = "yyyy-MM-dd";
    String dTime = "HH:mm";


    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat dateFormat = new SimpleDateFormat(dMonth);
    String getTime = dateFormat.format(date);
    String getTime2;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);


        //Todo   intent.putExtra("Number", 2);

        txtbtn = findViewById(R.id.txtbtn);
        Button Btn2 = findViewById(R.id.btn2);



        txt2 = findViewById(R.id.txt2);
        txt4 = findViewById(R.id.txt4);
        txt5 = findViewById(R.id.txt5);
        safecheckbtn  = findViewById(R.id.safecheckbtn );
        checkBox = findViewById(R.id.safecheck) ;
        inforcheck   = findViewById(R.id.inforcheck );
        inforbtn  = findViewById(R.id.inforbtn) ;


        txt2.setText(getTime2);


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

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                String dTime = "HH:mm";
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat dateFormat2 = new SimpleDateFormat(dTime);
                getTime2 = dateFormat2.format(date);
                txt2.setText(getTime2+"분");

                if(duration/60000 > 0 ) {
                    txt4.setText((duration / 60000) + "분");
                    txt5.setText(((duration / 60000) * 200)+1500 + "원");

                }if((duration/60000 <0 )){
                    Toast.makeText(getApplicationContext(),"시간이 올바르지 않습니다. 다시 선택해주세요",Toast.LENGTH_SHORT).show();
                }

            }
        };
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){}
                    handler.sendEmptyMessage(1);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();




        txtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(SelectActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,myTimePicker, hour, minute, false);
                dialog.setTitle("대여시작시간");
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });

        Btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(duration / 60000 == 0) {
                    Toast.makeText(getApplicationContext(),"시간 설정이 맞지않아요 ㅠ\n 시간을 다시 설정해 주세요.",Toast.LENGTH_SHORT).show();
                }else {
                    if(checkBox.isChecked()&& inforcheck.isChecked()){
                        showProgress("잠시만 기다려주세요....");
                        Intent intent = new Intent(SelectActivity.this, UsingActivity.class);
                        intent.putExtra("duration", duration);
                        startActivity(intent);
                        dismissProgress();
                        finish();
                    }else {
                        Toast.makeText(SelectActivity.this,"모든 내용을 확인 후 이용가능합니다.",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

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

    TimePickerDialog.OnTimeSetListener myTimePicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            txtbtn.setText(String.format("%02d:%02d",i,i1));


            // 문자 변환 코드
            // String.format("%02d:%02d",i,i1)


            try {
                SimpleDateFormat dataFormat = new SimpleDateFormat("kk:mm");

                Date startDate = dataFormat.parse(getTime2);
                Date endDate = dataFormat.parse(String.format("%02d:%02d",i,i1));
                duration = endDate.getTime() - startDate.getTime();

            }catch (Exception e){

            }
        }
    };


    private void showAlertDialog() {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.safe);


        AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity.this);
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
        dilaog01 = new Dialog(SelectActivity.this);
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