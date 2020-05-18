package com.mtc.mindbook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.playlist.BookshelfAdapter;
import com.mtc.mindbook.models.playlist.PlayListItem;

import java.util.ArrayList;
import java.util.List;

public class BookShelfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_shelf);
        final List<PlayListItem> playListItems = new ArrayList<>();
        playListItems.add(new PlayListItem("ds1", 10));
        playListItems.add(new PlayListItem("ds2", 11));
        playListItems.add(new PlayListItem("ds3", 2));
        final BookshelfAdapter adapter = new BookshelfAdapter(playListItems);
        RecyclerView listView = this.findViewById(R.id.list_playlists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
    }

}
