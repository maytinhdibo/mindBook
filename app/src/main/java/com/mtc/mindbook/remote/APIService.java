package com.mtc.mindbook.remote;

import com.mtc.mindbook.models.responseObj.DetailReponseObj;
import com.mtc.mindbook.models.responseObj.LoginResponseObj;
import com.mtc.mindbook.models.responseObj.user.UserResponseObj;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    @GET("books/details")
    Call<DetailReponseObj> detailBook(@Query("book_id") String bookId);

    @GET("user/profile")
    Call<UserResponseObj> fetchUserInfo(@Header("Authorization") String token);
}
