package com.jhn.googlemaptest.api;


import com.jhn.googlemaptest.model.ImageRes;
import com.jhn.googlemaptest.model.UserInfoRes;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ImageApi {
    // 이미지 S3에 저장하기
    @Multipart
    @POST("/dev/api/v1/posting")
    Call<ImageRes> PostingUpload(@Header("Authorization") String accessToken,
                           @Part MultipartBody.Part image);

    // 내정보 가져오기
    @GET("/dev/api/v1/user/me")
    Call<UserInfoRes> UserInfo(@Header("Authorization") String accessToken);

}
