package com.jhn.googlemaptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jhn.googlemaptest.api.ImageApi;
import com.jhn.googlemaptest.api.NetworkClient;
import com.jhn.googlemaptest.api.UserApi;
import com.jhn.googlemaptest.model.ImageRes;
import com.jhn.googlemaptest.model.UserInfoRes;
import com.jhn.googlemaptest.model.UserRes;
import com.jhn.googlemaptest.utils.Utils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity  implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    int height = 120;
    int width = 70;
    private ProgressDialog progressDialog;
    String accessToken;
    String User_email;
    String User_nickname;
    String getTitle;
    Dialog dilaog01;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView maintxt = findViewById(R.id.maintxt);
        maintxt.setSingleLine(true);    // 한줄로 표시하기
        maintxt.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        maintxt.setSelected(true);

        maintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imforDialog();
            }
        });


        Button button = findViewById(R.id.btn);
        ImageButton informationbtn = findViewById(R.id.informationbtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME, MODE_PRIVATE);
                accessToken = sp.getString("accessToken", "");

                if(accessToken.isEmpty()){
                    // 로그인 액티비티를 띄운다.
                    loginDilog();
                }else{
                    if(getTitle==null){
                        Toast.makeText(MainActivity.this,"전동 킥보드를 선택해주세요",Toast.LENGTH_SHORT).show();
                    }else {
                        showDialog();
                    }
                }

            }
        });

        informationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME, MODE_PRIVATE);
                accessToken = sp.getString("accessToken", "");
                if(accessToken.isEmpty()){
                    // 로그인 액티비티를 띄운다.
                    loginDilog();
                }else{

                    showProgress("내 정보 가져오는 중 입니다....");

                    Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
                    ImageApi api = retrofit.create(ImageApi.class);
                    Call<UserInfoRes> call = api.UserInfo("Bearer "+ accessToken);

                    Log.i("MyMemoApp", "2222");
                    call.enqueue(new Callback<UserInfoRes>() {
                        @Override
                        public void onResponse(Call<UserInfoRes> call, Response<UserInfoRes> response) {
                            dismissProgress();
                            if(response.isSuccessful()){
                                // 200 OK 인 경우
                                // 정상 저장되었으면, 이 액티비티는 끝낸다.
                                User_email = response.body().getUser_email();
                                User_nickname =response.body().getUser_nickname();
                                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                intent.putExtra("User_email",User_email);
                                intent.putExtra("User_nickname",User_nickname);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(MainActivity.this,
                                        "죄송합니다 현재 이용 불가 합니다 ㅠㅠ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<UserInfoRes> call, Throwable t) {
                            dismissProgress();
                            Toast.makeText(MainActivity.this,
                                    "죄송합니다 현재 이용 불가 합니다 ㅠ",
                                    Toast.LENGTH_SHORT).show();
                            return;

                        }
                    });
                }
            }
        });


    }
    LatLngBounds australiaBounds = new LatLngBounds(
            new LatLng(-44, 113), // SW bounds
            new LatLng(-10, 154)  // NE bounds
    );



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
       // map.setOnMarkerClickListener(markerClickListener);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.getCenter(), 17));




        // 구글맵 마커 사이즈 조절하는 코드
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.pin3);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.selectpin);
        Bitmap b2=bitmapdraw2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);


        // 임의로 전동 킥보드 넣는 코드
        LatLng scooter1 = new LatLng(37.544121, 126.677024);
        map.addMarker(new MarkerOptions()
                .position(scooter1)
                .title("1번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter1));

        LatLng scooter2 = new LatLng(37.543885, 126.676514);
        map.addMarker(new MarkerOptions()
                .position(scooter2)
                .title("2번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter2));

        LatLng scooter3 = new LatLng(37.543158, 126.676594);
        map.addMarker(new MarkerOptions()
                .position(scooter3)
                .title("3번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter3));

        LatLng scooter4 = new LatLng(37.543921, 126.675982);
        map.addMarker(new MarkerOptions()
                .position(scooter4)
                .title("4번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter4));

        LatLng scooter5 = new LatLng(37.543168, 126.677016);
        map.addMarker(new MarkerOptions()
                .position(scooter5)
                .title("5번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter5));

        LatLng scooter6 = new LatLng(37.542365, 126.676864);
        map.addMarker(new MarkerOptions()
                .position(scooter6)
                .title("6번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter6));


        LatLng scooter7 = new LatLng(37.54141629845835, 126.67638207268163);
        map.addMarker(new MarkerOptions()
                .position(scooter7)
                .title("7번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter7));

        LatLng scooter8 = new LatLng(37.54190695319576, 126.6769434602541);
        map.addMarker(new MarkerOptions()
                .position(scooter8)
                .title("8번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter8));

        LatLng scooter9 = new LatLng(37.5424009037762, 126.67650164084371);
        map.addMarker(new MarkerOptions()
                .position(scooter9)
                .title("9번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter9));

        LatLng scooter10 = new LatLng(37.54275468512426, 126.67692267278447);
        map.addMarker(new MarkerOptions()
                .position(scooter10)
                .title("10번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter10));

        LatLng scooter11 = new LatLng(37.542103722304525, 126.67758577802353);
        map.addMarker(new MarkerOptions()
                .position(scooter11)
                .title("11번 전동킥보드")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(scooter11));


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String marker_id=marker.getId();

                if(marker_id != null){
                    //marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker2));
                    getTitle = marker.getTitle();
                    Toast.makeText(MainActivity.this, getTitle, Toast.LENGTH_SHORT).show();

                }else{

                }

                return false;
            }

        });

    }


    @Override
    public void onMyLocationClick(@NonNull Location location) { }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "내 위치보기", Toast.LENGTH_SHORT)
                .show();

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    private void loginDilog(){
        dilaog01 = new Dialog(MainActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_login_dialog);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));

        Button btn = dilaog01.findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선불 이용할 경우
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                dilaog01.dismiss();
            }
        });


    }
    private void showDialog(){
        dilaog01 = new Dialog(MainActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_main_dialog);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));

        TextView select_txt = dilaog01.findViewById(R.id.select_txt);
        select_txt.setText(getTitle+"선택됨");
        Button firstbtn = dilaog01.findViewById(R.id.firstbtn);
        Button lastbtn = dilaog01.findViewById(R.id.lastbtn);


        firstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선불 이용할 경우
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(intent);
            }
        });
            // 후불 이용 ㅅ ㅣ
        lastbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectActivity2.class);
                startActivity(intent);
            }
        });


    }
    private void imforDialog(){
        dilaog01 = new Dialog(MainActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_fristinformation_dialog);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));

        Button startbtn = dilaog01.findViewById(R.id.startbtn);

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dilaog01.dismiss();
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

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            finish();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "종료하려면 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show();
        }
    }

}