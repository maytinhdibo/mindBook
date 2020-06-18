package com.mtc.mindbook.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.core.os.ConfigurationCompat;

import com.mtc.mindbook.DetailActivity;
import com.mtc.mindbook.PlaylistActivity;
import com.mtc.mindbook.R;
import com.mtc.mindbook.SearchActivity;
import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDataResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Utils {
    public static void openDetailPage(Context context, String id) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("EXTRA_BOOK_ID", id);
        context.startActivity(intent);
    }

    public static void openSearchPage(Context context, String tag) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("EXTRA_TAG", tag);
        context.startActivity(intent);
    }

    public static Locale getLocale() {
        return ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
    }

    public static String getLocaleToString() {
        return getLocale().toString();
    }

    public static String convertDistance(float meter) {
        if (meter < 1000) {
            return String.format("%.1f", meter) + "m";
        } else {
            return String.format("%.1f", meter / 1000) + "km";
        }
    }

    public static void addBookToNewPlaylist(Context context, String name, String bookId) {
        APIService apiService = APIUtils.getUserService();
        SharedPreferences sharedPrefs = context.getSharedPreferences("userDataPrefs", MODE_PRIVATE);
        String token = "Bearer " + sharedPrefs.getString("accessToken", "");
        Call<DefaultResponseObj> addNewPlaylist = apiService.newPlaylist(token, name);
        addNewPlaylist.enqueue(new Callback<DefaultResponseObj>() {
            @Override
            public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                if (!response.body().getMesssage().equals("success")) {
                    Toast.makeText(context, "Tạo playlist không thành công", Toast.LENGTH_SHORT).show();
                }
                Call<PlaylistResponseObj> fetchBookshelf = apiService.getUserPlaylistList(token);
                fetchBookshelf.enqueue(new Callback<PlaylistResponseObj>() {
                    @Override
                    public void onResponse(Call<PlaylistResponseObj> call, Response<PlaylistResponseObj> response) {
                        if (response.body() == null) {
                            onFailure(call, null);
                            return;
                        }
                        List<PlaylistDataResponseObj> playlists = response.body().getData();
                        Integer playlistId = playlists.get(playlists.size() - 1).getPlaylistId();
                        Call<DefaultResponseObj> addBook = apiService.addBookToPlaylist(token, playlistId, bookId);
                        addBook.enqueue(new Callback<DefaultResponseObj>() {
                            @Override
                            public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                                if (response.body() == null) {
                                    onFailure(call, null);
                                    return;
                                }
                                if (response.body().getMesssage().equals("Success")) {
                                    Toast.makeText(context, "Đã thêm vào " + playlists.get(playlists.size() - 1).getName(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                                Log.d("failed", "onFailure: " + t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<PlaylistResponseObj> call, Throwable t) {
                        Toast.makeText(context, R.string.err_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static void openPlaylistDetail(Context context, Integer playlistId, String title) {
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra("playlistId", playlistId);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }
}
