package com.mtc.mindbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.playlist.PlaylistDetailAdapter;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDetailItem;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDetailResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistActivity extends AppCompatActivity {
    TextView title = null;
    RecyclerView bookList = null;
    private Integer playlistId;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        findViewById(R.id.playlist_title).setPaddingRelative(0, getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android")
        ), 0, 0);

        title = findViewById(R.id.playlist_title);
        bookList = findViewById(R.id.playlist_detail_book_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bookList.setLayoutManager(layoutManager);

        Intent parentIntent = getIntent();
        playlistId = parentIntent.getIntExtra("playlistId", 1);
        String playlistTitle = parentIntent.getStringExtra("title");

        title.setText(playlistTitle);

        APIService userService = APIUtils.getUserService();
        SharedPreferences sharedPrefs = getSharedPreferences("userDataPrefs", MODE_PRIVATE);
        String token = "Bearer " + sharedPrefs.getString("accessToken", "");

        Call<PlaylistDetailResponseObj> fetchPlaylistDetail = userService.getPlaylistDetail(token, playlistId, "vi");
        fetchPlaylistDetail.enqueue(new Callback<PlaylistDetailResponseObj>() {
            @Override
            public void onResponse(Call<PlaylistDetailResponseObj> call, Response<PlaylistDetailResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                List<PlaylistDetailItem> books = response.body().getData();
                PlaylistDetailAdapter adapter = new PlaylistDetailAdapter(books, playlistId);
                bookList.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<PlaylistDetailResponseObj> call, Throwable t) {
                Log.d("d", "onFailure: " + t.getMessage());
            }
        });
    }

    public void resetActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
