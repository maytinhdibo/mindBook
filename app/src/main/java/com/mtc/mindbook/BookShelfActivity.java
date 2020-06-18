package com.mtc.mindbook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtc.mindbook.models.playlist.BookshelfAdapter;
import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDataResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookShelfActivity extends AppCompatActivity {
    private Dialog newPlaylistDialog = null;
    private Context context = this;
    APIService userService = null;
    SharedPreferences sharedPrefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_shelf);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        findViewById(R.id.title_bar).setPaddingRelative(0, getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android")
        ), 0, 0);

        sharedPrefs = getSharedPreferences("userDataPrefs", MODE_PRIVATE);

        FloatingActionButton fab = findViewById(R.id.new_playlist_button);
        Button submitNewPlaylistDialog = findViewById(R.id.submit_new_playlist_dialog);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView listView = this.findViewById(R.id.list_playlists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);
        String token = "Bearer " + sharedPrefs.getString("accessToken", "");
        userService = APIUtils.getUserService();
        Call<PlaylistResponseObj> fetchBookshelf = userService.getUserPlaylistList(token);
        fetchBookshelf.enqueue(new Callback<PlaylistResponseObj>() {
            @Override
            public void onResponse(Call<PlaylistResponseObj> call, Response<PlaylistResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                List<PlaylistDataResponseObj> playlists = response.body().getData();
                BookshelfAdapter adapter = new BookshelfAdapter(playlists, context);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<PlaylistResponseObj> call, Throwable t) {
                Toast.makeText(BookShelfActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onNewPlaylistClicked(View view) {
        newPlaylistDialog = new Dialog(context);
        newPlaylistDialog.setContentView(R.layout.new_playlist_dialog);
        newPlaylistDialog.show();
    }

    public void onCancelDialog(View view) {
        newPlaylistDialog.dismiss();
    }

    public void onPlaylistSubmit(View view) {
        EditText newPlaylistName = newPlaylistDialog.findViewById(R.id.new_playlist_name);
        String name = newPlaylistName.getText().toString();
        if (name.equals("") || name.equals(" ")) {
            Toast.makeText(getBaseContext(), "Tên playlist không được để trống.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String token = "Bearer " + sharedPrefs.getString("accessToken", "");
            Call<DefaultResponseObj> addNewPlaylist = userService.newPlaylist(token, newPlaylistName.getText().toString());
            addNewPlaylist.enqueue(new Callback<DefaultResponseObj>() {
                @Override
                public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                    if (!response.body().getMesssage().equals("success")) {
                        Toast.makeText(context, "Tạo playlist không thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
            newPlaylistDialog.dismiss();
        }
    }
}
