package com.mtc.mindbook.remote;

import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.LoginResponseObj;
import com.mtc.mindbook.models.responseObj.banner.BannerResponseObj;
import com.mtc.mindbook.models.responseObj.catetory.trending.BookTrendResponseObj;
import com.mtc.mindbook.models.responseObj.detail.DetailReponseObj;
import com.mtc.mindbook.models.responseObj.explore.near.NearbyResponseObj;
import com.mtc.mindbook.models.responseObj.explore.share.ShareItemResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDetailResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistResponseObj;
import com.mtc.mindbook.models.responseObj.rating.RatingCommentsResponseObj;
import com.mtc.mindbook.models.responseObj.search.SearchResponseObj;
import com.mtc.mindbook.models.responseObj.user.UserResponseObj;

import retrofit2.Call;
import retrofit2.http.DELETE;
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

    @GET("books/trending?limit=5&page=1")
    Call<BookTrendResponseObj> trending();

    @GET("search/")
    Call<SearchResponseObj> search(@Query("text") String searchQuery);

    @GET("recommend/random")
    Call<SearchResponseObj> random();

    @GET("recommend/for_you")
    Call<SearchResponseObj> forYou(@Header("Authorization") String token);

    @GET("banner/all?limit=5&page=1")
    Call<BannerResponseObj> getBanners();

    @GET("ratings/new?limit=5")
    Call<ShareItemResponseObj> getShares(@Query("page") int page);

    @GET("user/nearby?limit=2&radius=2000000")
    Call<NearbyResponseObj> getNearBy(@Header("Authorization") String token,
                                      @Query("page") int page);

    @GET("/user/playlist")
    Call<PlaylistResponseObj> getUserPlaylistList(@Header("Authorization") String token);

    @POST("/user/playlist")
    Call<DefaultResponseObj> newPlaylist(@Header("Authorization") String token,
                                         @Query("playlist_name") String playlistName);

    @GET("/user/playlist/detail")
    Call<PlaylistDetailResponseObj> getPlaylistDetail(@Header("Authorization") String token,
                                                      @Query("playlist_id") Integer playlistId,
                                                      @Query("lang") String language);

    @POST("/user/playlist/detail")
    Call<DefaultResponseObj> addBookToPlaylist(@Header("Authorization") String token,
                                               @Query("playlist_id") Integer playlistId,
                                               @Query("book_id") String bookId);

    @DELETE("/user/playlist/detail")
    Call<DefaultResponseObj> deleteBookFromPlaylist(@Header("Authorization") String token,
                                                    @Query("playlist_id") Integer playlistId,
                                                    @Query("book_id") String bookId);

    @GET("books/ratings")
    Call<RatingCommentsResponseObj> getRatingComment(@Query("book_id") String bookId,
                                                     @Query("limit") int limit,
                                                     @Query("page") int page);

    @GET("books/details")
    Call<DetailReponseObj> detailBook(@Query("book_id") String bookId);

    @POST("/user/rate")
    Call<DefaultResponseObj> rateBook(@Header("Authorization") String token,
                                      @Query("book_id") String bookId,
                                      @Query("rating_num") int RateNum,
                                      @Query("rating_comment") String RateComment);

    @POST("/user/position/update")
    Call<DefaultResponseObj> updateLocation(@Header("Authorization") String token,
                                            @Query("latitude") double latitude,
                                            @Query("longitude") double longitude);

    @POST("/user/share")
    Call<DefaultResponseObj> shareBook(@Header("Authorization") String token,
                                       @Query("book_id") String bookId);

    @POST("/user/update/latest_book")
    Call<DefaultResponseObj> latestBook(@Header("Authorization") String token,
                                        @Query("book_id") String bookId);

    @GET("user/profile")
    Call<UserResponseObj> fetchUserInfo(@Header("Authorization") String token);
}
