package com.mtc.mindbook;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        Player player = new Player((SeekBar) findViewById(R.id.seekBar));
        player.start();


    }
}

class Player extends Thread {
    private static SeekBar bar;

    public Player(SeekBar bar) {
        this.bar = bar;
    }

    public void run() {
        String url = "https://r1---ca.nixcdn.com/Singer_Audio5/NguoiTungYeuAnhRatSauNang-HuongTram-4101534.mp3?st=r2FC5GIUbXW4efX1I4zFhg&e=1584713510";
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        double curPro =0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                curPro = ((double) (mediaPlayer.getCurrentPosition() )/ mediaPlayer.getDuration() )*100;

                bar.setProgress((int) (curPro), true);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        while (!Thread.interrupted()) {
            // do something interesting.
        }


    }
}