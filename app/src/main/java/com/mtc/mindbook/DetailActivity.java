package com.mtc.mindbook;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_TYPE = "none";
    public static final String EXTRA_MESSAGE_ID = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Window window = getWindow();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



        View view = findViewById(android.R.id.content).getRootView();

        Button readButton = (Button) findViewById(R.id.read_btn);
        Button listenButton = (Button) findViewById(R.id.listen_btn);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);

                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE_TYPE", "read");
                extras.putString("EXTRA_MESSAGE_ID", "22");
                intent.putExtras(extras);

                startActivity(intent);
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);

                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE_TYPE", "listen");
                extras.putString("EXTRA_MESSAGE_ID", "3");
                intent.putExtras(extras);

                startActivity(intent);
            }
        });


        Picasso.get()
                .load("https://dspncdn.com/a1/media/692x/38/3a/21/383a215d646b32b00b3b44b58bd81fa1.jpg")
                .transform(new BlurTransformation(getBaseContext(), 75, 2))
                .into((ImageView) findViewById(R.id.blur_bg));

        Picasso.get()
                .load("https://dspncdn.com/a1/media/692x/38/3a/21/383a215d646b32b00b3b44b58bd81fa1.jpg")
                .into((ImageView) findViewById(R.id.cover));

        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        bottomBar.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT
                , 185 + this.getNavigationBarHeight()));

//        Intent intent = new Intent(view.getContext(), ReaderActivity.class);
////        intent.putExtra(EXTRA_MESSAGE, data.get(position).getName());
//        view.getContext().startActivity(intent);

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

    public int getNavigationBarHeight() {
        Resources resources = this.getBaseContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
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

        double curPro = 0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                curPro = ((double) (mediaPlayer.getCurrentPosition()) / mediaPlayer.getDuration()) * 100;

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