package com.mtc.mindbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity {
    TextView title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        title = findViewById(R.id.playlist_title);
        Intent parentIntent = getIntent();
        String playlistTitle = parentIntent.getStringExtra("title");
        title.setText(playlistTitle);
    }
}
