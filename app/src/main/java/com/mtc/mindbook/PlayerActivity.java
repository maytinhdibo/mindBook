package com.mtc.mindbook;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mtc.mindbook.models.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ProgressBar progress_bar;
    ImageButton buttonPlayPause;
    TextView title;
    TextView author;
    TextView currentTime;
    TextView allTime;

    private MediaObserver observer = null;

    private List<Track> chapters;

    private class MediaObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                progress_bar.setProgress((int)((double)mediaPlayer.getCurrentPosition() / (double)mediaPlayer.getDuration()*100));
//                int millisecond = mediaPlayer.getCurrentPosition();
//                int mm = millisecond / 60000;
//                int ss = (millisecond % 60000) / 1000;
//                currentTime.setText(mm + ":" + ss);
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    System.err.println(ex);
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        chapters = Arrays.asList(
                new Track("Lời Giới Thiệu", "Napoleon Hill", "https://sv.sachmp3.com/data/think-sachmp3.com/1-sachmp3.com.mp3", "https://sachmp3.com/wp-content/uploads/2019/07/THINK-AND-GROW-RICH-NGH─¿-GI├ÇU-V├Ç-L├ÇM-GI├ÇU.jpg"),
                new Track("Chương 01 - Khát Vọng", "Napoleon Hill", "https://sv.sachmp3.com/data/think-sachmp3.com/3-sachmp3.com.mp3", "https://sachmp3.com/wp-content/uploads/2019/07/THINK-AND-GROW-RICH-NGH─¿-GI├ÇU-V├Ç-L├ÇM-GI├ÇU.jpg")
        );

        title = findViewById(R.id.listen_chap_title);
        title.setText(chapters.get(0).getTitle());

        author = findViewById(R.id.listen_author);
        author.setText(chapters.get(0).getAuthor());

        allTime = findViewById(R.id.listen_all_time);

        Picasso.get()
                .load(chapters.get(0).getCoverLink())
                .transform(new BlurTransformation(getBaseContext(), 18, 2))
                .into((ImageView) findViewById(R.id.listen_blur_bg));

        Picasso.get()
                .load(chapters.get(0).getCoverLink())
                .into((ImageView) findViewById(R.id.listen_cover));


    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        currentTime = findViewById(R.id.listen_current_time);


        buttonPlayPause = findViewById(R.id.button_play_pause);
        buttonPlayPause.setEnabled(false);
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    buttonPlayPause.setImageResource(R.drawable.ic_play_circle_outline);
                } else {
                    mediaPlayer.start();
                    buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
                }
            }
        });
        try {
            mediaPlayer.setDataSource(chapters.get(0).getMp3Link());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                observer.stop();
                progress_bar.setProgress(mp.getCurrentPosition());
                // TODO Auto-generated method stub
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                progress_bar.setSecondaryProgress(percent);
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
                allTime.setText(minisecondToMinute(mediaPlayer.getDuration()));
                buttonPlayPause.setEnabled(true);

            }
        });
        observer = new MediaObserver();
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

//        Thread thread = new Thread() {
//            @Override
//            public void start() {
//                runOnUiThread(observer);
//            }
//        };
        new Thread(observer).start();
    }

    private String minisecondToMinute(int millisecond) {
        System.out.println("Millisecond: " + millisecond);
        int mm = millisecond / 60000;
        int ss = (millisecond % 60000) / 1000;
        return mm + ":" + ss;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}
