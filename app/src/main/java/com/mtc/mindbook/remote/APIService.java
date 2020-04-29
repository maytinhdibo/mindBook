package com.mtc.mindbook.remote;

import com.mtc.mindbook.models.responseObj.LoginResponseObj;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {
    @POST("auth/login")
    @FormUrlEncoded
    Call<LoginResponseObj> login(@Field("username") String username, @Field("password") String password);

    @POST("auth/registration")
    @FormUrlEncoded
    Call<LoginResponseObj> register(@Field("username") String username,
                                    @Field("firstname") String firstname,
                                    @Field("lastname") String lastname,
                                    @Field("email") String email,
                                    @Field("password") String password);
}
