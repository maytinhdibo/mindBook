package com.mtc.mindbook;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtc.mindbook.models.playlist.BookshelfAdapter;
import com.mtc.mindbook.models.playlist.PlayListItem;

import java.util.ArrayList;
import java.util.List;

public class BookShelfActivity extends AppCompatActivity {
    private Dialog newPlaylistDialog = null;
    private Context context = this;
    private List<PlayListItem> playListItems = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_shelf);
        playListItems = new ArrayList<>();
        playListItems.add(new PlayListItem("Danh sách yêu thích", 10));
        playListItems.add(new PlayListItem("Sách khoa học", 11));
        playListItems.add(new PlayListItem("Tiểu thuyết", 2));
        final BookshelfAdapter adapter = new BookshelfAdapter(playListItems);
        RecyclerView listView = this.findViewById(R.id.list_playlists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
        FloatingActionButton fab = findViewById(R.id.new_playlist_button);
        Button submitNewPlaylistDialog = findViewById(R.id.submit_new_playlist_dialog);
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
        Log.d("e", "asdsd " + name);
        if (name.equals("") || name.equals(" ")) {
            Toast.makeText(getBaseContext(), "Tên playlist không được để trống.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            playListItems.add(new PlayListItem(name, 0));
            newPlaylistDialog.dismiss();
        }
    }
}
