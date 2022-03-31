package com.jhn.googlemaptest.api;

import com.jhn.googlemaptest.model.UserReq;
import com.jhn.googlemaptest.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {
    // 로그인 API
    @POST("/dev/api/v1/user/login")
    Call<UserRes> userLogin(@Body UserReq userReq);

    // 로그아웃 API
    @POST("/dev/api/v1/user/logout")
    Call<UserRes> userLogout(@Header("Authorization") String accessToken);

    // 회원가입 API
    @POST("/dev/api/v1/user/login")
    Call<UserRes> userSignUp(@Body UserReq userReq);

}
