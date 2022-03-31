package com.jhn.googlemaptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhn.googlemaptest.api.ImageApi;
import com.jhn.googlemaptest.api.NetworkClient;
import com.jhn.googlemaptest.model.ImageRes;
import com.jhn.googlemaptest.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ResultActivity extends AppCompatActivity {

    TextView txt;
    TextView txt2;
    TextView txt4;
    TextView awstxt;
    TextView infortxt;

    Button btn;
    Button resultbtn;

    ImageView imgPhoto;

    String againtext;
    long resultpay;
    String Answer;
    Dialog dilaog01;
    Bitmap photo;

    int Number;
    long duration;

    private ProgressDialog progressDialog;
    private File photoFile;
    String dMonth = "yyyy-MM-dd";

//    String[] Answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txt = findViewById(R.id.txt);
        txt2 = findViewById(R.id.txt2);
        txt4 = findViewById(R.id.txt4);
        infortxt = findViewById(R.id.infortxt);
        awstxt = findViewById(R.id.awstxt);
        btn = findViewById(R.id.btn);
        resultbtn = findViewById(R.id.resultbtn);
        imgPhoto = findViewById(R.id.imgPhoto);


        Intent intent = getIntent();
        duration = intent.getExtras().getLong("duration");

        txt.setText(duration/60000+"분");
        txt2.setText(((duration/60000)*200)+1500+"원");

        resultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlertDialog();

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");

                if (accessToken != null) {
                    showProgress("사진 저장 중입니다...");
                    Retrofit retrofit = NetworkClient.getRetrofitClient(ResultActivity.this);
                    ImageApi api = retrofit.create(ImageApi.class);
                    Log.i("MyMemoApp", "1111");

                    //todo  -> fileBody에 값이 없는 경우 Toast 를 만드는 법 알아내기

                    if (photoFile != null) {
//               MemoReq memoReq = new MemoReq(title, datetime, content);
                        RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/*"));
                        MultipartBody.Part part = MultipartBody.Part.createFormData(
                                "image", photoFile.getName(), fileBody
                        );
                        Log.i("MyMemoApp", photoFile.getName());


                        Call<ImageRes> call = api.PostingUpload("Bearer " + accessToken, part);
                        Log.i("MyMemoApp", "2222");

                        call.enqueue(new Callback<ImageRes>() {

                            @Override
                            public void onResponse(Call<ImageRes> call, Response<ImageRes> response) {
                                dismissProgress();
                                if (response.isSuccessful()) {
                                    // 200 OK 인 경우
                                    // 정상 저장되었으면, 이 액티비티는 끝낸다.
                                    Toast.makeText(ResultActivity.this,
                                            "사진 전송 완료",
                                            Toast.LENGTH_SHORT).show();

                                    Answer = response.body().getAnswer();
                                    Number = response.body().getNumber();
                                    awstxt.setText(Answer);
                                    // 불법 주차
                                    if (Number == 200) {
                                        txt2.setText(((duration / 60000) * 200) + 4000 + "원");
                                        resultpay = ((duration / 60000) * 200) + 4000 ;
                                        infortxt.setText("주차 구역이 올바르지 않아요!!!\n추가 금액(2500원)이 부과됩니다!");
                                        // 킥보드 안보임
                                    }
                                    if (Number == 202) {
                                        txt2.setText(((duration / 60000) * 200) + 1500 + "원");
                                        resultpay = ((duration / 60000) * 200) + 1500 ;
                                        infortxt.setText("주차공간에 문제가 없으면\n할인해드려요");
                                        // 잘 주차한 경우 201
                                    }
                                    if (Number == 201) {
                                        txt2.setText(((duration / 60000) * 200) + 500 + "원");
                                        resultpay = ((duration / 60000) * 200) + 500;
                                        infortxt.setText("잘 주차하셔서 \n할인해드립니다!!");
                                    }


                                } else {
                                    Toast.makeText(ResultActivity.this,
                                            "연결 안됨",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<ImageRes> call, Throwable t) {
                                dismissProgress();
                                Toast.makeText(ResultActivity.this,
                                        "네트워크 문제 발생",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        dismissProgress();
                        Toast.makeText(ResultActivity.this,
                                "사진을 업로드 해주세요!",
                                Toast.LENGTH_SHORT).show();

                    }
                }else{
                    // 로그인이 안돼있을 때
                    Toast.makeText(ResultActivity.this, "로그인 후 다시 입력하세요. ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
                }
            }
        });


        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 알러트 다이얼로그 띄운다. (사진찍기 / 앨범에서 선택)
                showDialog();
            }
        });

    }

    private void showDialog() {
        dilaog01 = new Dialog(ResultActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_select_image);
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));
        dilaog01.show();

        CardView camerabtn = dilaog01.findViewById(R.id.camerabtn);
        CardView gallerybtn = dilaog01.findViewById(R.id.gallerybtn);



        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 카메라를 선택하는 경우
                camera();
                dilaog01.dismiss();
            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 앨범에서 사진 가져오는 함수 실행
                album();
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


    private void camera(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                ResultActivity.this, Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ResultActivity.this,
                    new String[]{Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(ResultActivity.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(ResultActivity.this.getPackageManager())  != null  ){
                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                Uri fileProvider = FileProvider.getUriForFile(ResultActivity.this,
                        "com.jhn.googlemaptest.fileprovider", photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                startActivityForResult(i, 100);

            } else{
                Toast.makeText(ResultActivity.this, "이폰에는 카메라 앱이 없습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }


    }
    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private void album(){
        if(checkPermission()){
            displayFileChoose();
        }else{
            requestPermission();
        }
    }
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(ResultActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(ResultActivity.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(ResultActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(ResultActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ResultActivity.this, "11권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResultActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ResultActivity.this, "22 권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResultActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK){


            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            // 압축시킨다. 해상도 낮춰서
            OutputStream os;
            try {
                os = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            imgPhoto.setImageBitmap(photo);
//            imgPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
//            currentImg.setBackgroundResource(R.drawable.image_border);




        }else if(requestCode == 300 && resultCode == RESULT_OK && data != null &&
                data.getData() != null){

            Uri albumUri = data.getData( );
            String fileName = getFileName( albumUri );
            try {

                ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;
                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );
                photoFile = new File( this.getCacheDir( ), fileName );
                FileOutputStream outputStream = new FileOutputStream( photoFile );
                IOUtils.copy( inputStream, outputStream );

//                //임시파일 생성
//                File file = createImgCacheFile( );
//                String cacheFilePath = file.getAbsolutePath( );


                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                imgPhoto.setImageBitmap(photo);

            } catch ( Exception e ) {
                e.printStackTrace( );
            }


        }

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 0){
            setResult(0);
            finish();
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    // 파일의 내용을 읽어와서, 임시파일 만들기 위함.
    void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
    private Bitmap resize(Context context, Uri uri, int resize) {
        Bitmap resizeBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap = bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }

    //앨범에서 선택한 사진이름 가져오기
    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }

    //이미지뷰에 뿌려질 앨범 비트맵 반환
    public Bitmap getBitmapAlbum( View targetView, Uri uri ) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( uri, "r" );
            if ( parcelFileDescriptor == null ) return null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor( );
            if ( fileDescriptor == null ) return null;

            int targetW = targetView.getWidth( );
            int targetH = targetView.getHeight( );

            BitmapFactory.Options options = new BitmapFactory.Options( );
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            int photoW = options.outWidth;
            int photoH = options.outHeight;

            int scaleFactor = Math.min( photoW / targetW, photoH / targetH );
            if ( scaleFactor >= 8 ) {
                options.inSampleSize = 8;
            } else if ( scaleFactor >= 4 ) {
                options.inSampleSize = 4;
            } else {
                options.inSampleSize = 2;
            }
            options.inJustDecodeBounds = false;

            Bitmap reSizeBit = BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            ExifInterface exifInterface = null;
            try {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
                    exifInterface = new ExifInterface( fileDescriptor );
                }
            } catch ( IOException e ) {
                e.printStackTrace( );
            }

            int exifOrientation;
            int exifDegree = 0;

            //사진 회전값 구하기
            if ( exifInterface != null ) {
                exifOrientation = exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL );

                if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 ) {
                    exifDegree = 90;
                } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_180 ) {
                    exifDegree = 180;
                } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_270 ) {
                    exifDegree = 270;
                }
            }

            parcelFileDescriptor.close( );
            Matrix matrix = new Matrix( );
            matrix.postRotate( exifDegree );

            Bitmap reSizeExifBitmap = Bitmap.createBitmap( reSizeBit, 0, 0, reSizeBit.getWidth( ), reSizeBit.getHeight( ), matrix, true );
            return reSizeExifBitmap;

        } catch ( Exception e ) {
            e.printStackTrace( );
            return null;
        }


    }

    //캐시파일 생성
    public File createImgCacheFile( ) {
        File cacheFile = new File( getCacheDir( ), new SimpleDateFormat( "yyyyMMdd_HHmmss", Locale.US ).format( new Date( ) ) + ".jpg" );
        return cacheFile;
    }

    private void showAlertDialog() {
        // 주차 금지 200
        if (Number == 200) {
            againtext = "주차금지 구역이예요!!\n주차구역에 주차하면 3000원 할인 받을 수 있어요!!";
            resultagainDialog();

            // 킥보드 안보임 201
        }
        if (Number == 202) {
            againtext = "킥보드가 보이지 않아요 ㅠㅠ\n사진을 잘찍으면 할인 해택을 받을 수 있어요!!";
            resultagainDialog();
            // 잘 주차한 경우 201
        }
        if (Number == 201) {
            resultshowDialog();
        }
        if (Number == 0) {
            nophotodialog();
        }
    }
    private void resultagainDialog(){
        dilaog01 = new Dialog(ResultActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_result_dialog2);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));

        Button yesbtn = dilaog01.findViewById(R.id.yesbtn);
        Button nobtn = dilaog01.findViewById(R.id.nobtn);
        TextView againtxt = dilaog01.findViewById(R.id.againtxt);
        againtxt.setText(againtext);
        // 그냥 반납하기
        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultshowDialog();
            }
        });
        // 다시 찍기
        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dilaog01.dismiss();
            }
        });
    }
    private void resultshowDialog(){
        dilaog01 = new Dialog(ResultActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_final_dialog);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));


        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat(dMonth);
        String getTime = dateFormat.format(date);

        Button btn = dilaog01.findViewById(R.id.btn);
        TextView daytxt = dilaog01.findViewById(R.id.daytxt);
        TextView paytxt = dilaog01.findViewById(R.id.paytxt);
        TextView timetxt = dilaog01.findViewById(R.id.timetxt);

        daytxt.setText("today   "+getTime);
        paytxt.setText(resultpay+"원");
        timetxt.setText(duration/60000+"분");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void nophotodialog(){
        dilaog01 = new Dialog(ResultActivity.this);
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.activity_nophoto_dialog);
        dilaog01.show();
        dilaog01.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background_round));


        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat(dMonth);
        String getTime = dateFormat.format(date);

        //그냥 내기
        Button yesbtn = dilaog01.findViewById(R.id.yesbtn);
        //이미지 업로드
        Button nobtn = dilaog01.findViewById(R.id.nobtn);


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultshowDialog();
            }
        });
        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dilaog01.dismiss();
            }
        });

    }

}