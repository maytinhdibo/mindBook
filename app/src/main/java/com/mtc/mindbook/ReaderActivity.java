package com.mtc.mindbook;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Bundle extras = getIntent().getExtras();
        String type = extras.getString("EXTRA_MESSAGE_TYPE");
        String id = extras.getString("EXTRA_MESSAGE_ID");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.reader);
        textView.setText("Type open is: " + type + ", ID: "+id);

//        Window window = getWindow();
//
//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//

//
//        Intent intent = getIntent();
//        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//
//        // Capture the layout's TextView and set the string as its text
//        TextView textView = findViewById(R.id.textView);
//        textView.setText(message);

//        Player player = new Player((SeekBar) findViewById(R.id.seekBar));
//        player.start();


    }
}
