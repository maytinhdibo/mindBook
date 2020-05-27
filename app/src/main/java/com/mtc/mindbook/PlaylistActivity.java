package com.mtc.mindbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity {
    TextView title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        findViewById(R.id.playlist_title).setPaddingRelative(0, getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android")
        ), 0, 0);

        title = findViewById(R.id.playlist_title);
        Intent parentIntent = getIntent();
        String playlistTitle = parentIntent.getStringExtra("title");
        title.setText(playlistTitle);
    }
}
