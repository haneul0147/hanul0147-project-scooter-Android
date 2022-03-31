package com.jhn.googlemaptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhn.googlemaptest.api.NetworkClient;
import com.jhn.googlemaptest.api.UserApi;
import com.jhn.googlemaptest.model.UserRes;
import com.jhn.googlemaptest.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserActivity extends AppCompatActivity {


    String User_email;
    String User_nickname;
    String accessToken;

    Button logoutbtn;

    Button btn;
    ImageView imagebackbtn;


    ProgressDialog progressDialog;

    TextView txt1;
    TextView txt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txt1=findViewById(R.id.txt1);
        txt2=findViewById(R.id.txt2);
        logoutbtn=findViewById(R.id.logoutbtn);
        btn=findViewById(R.id.btn);
        imagebackbtn = findViewById(R.id.imagebackbtn);


        Intent intent = getIntent();
        User_email = intent.getExtras().getString("User_email");
        User_nickname = intent.getExtras().getString("User_nickname");

        txt1.setText(User_email);
        txt2.setText(User_nickname+"님");

        SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME, MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");


        imagebackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserActivity.this,"서비스 준비 중 입니다.",Toast.LENGTH_SHORT).show();
            }
        });


        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accessToken.isEmpty()) {
                    // 버튼의 text가 login이면 로그인 액티비티 띄우기
                    Toast.makeText(UserActivity.this," 오류 : 토큰 값을 가지고 있지 않습니다",Toast.LENGTH_SHORT).show();
                } else{
                    // 버튼의 text가 logout이면 로그아웃 하기
                    showProgress("로그아웃 중입니다...");
                    Retrofit retrofit = NetworkClient.getRetrofitClient(UserActivity.this);
                    UserApi api = retrofit.create(UserApi.class);
                    Call<UserRes> call = api.userLogout("Bearer" + accessToken);
                    call.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                            dismissProgress();
                            accessToken = null;
                            // 토큰값 지우기
                            SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.commit();
                            Toast.makeText(UserActivity.this,"로그 아웃 성공",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserActivity.this, MainActivity.class);
                            startActivity(intent);

                        }
                        @Override
                        public void onFailure(Call<UserRes> call, Throwable t) {
                            Toast.makeText(UserActivity.this,"서비스 오류 \n 로그아웃이 되지않습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
    private void showProgress(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    private void dismissProgress(){
        progressDialog.dismiss();
    }

}