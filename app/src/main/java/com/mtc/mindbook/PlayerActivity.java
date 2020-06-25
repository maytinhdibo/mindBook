package com.mtc.mindbook;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mtc.mindbook.models.Track;
import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.detail.BookDetail;
import com.mtc.mindbook.models.responseObj.detail.DetailReponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
import com.mtc.mindbook.services.notification.OnClearFromRecentService;
import com.mtc.mindbook.utils.CreateNotification;
import com.mtc.mindbook.utils.threadUtils.NotifyingThread;
import com.mtc.mindbook.utils.threadUtils.ThreadCompleteListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    SeekBar progress_bar;
    ImageButton buttonPlayPause;
    TextView title;
    TextView author;
    TextView currentTime;
    TextView allTime;
    ProgressBar spinKit;
    ImageButton nextBtn;
    ImageButton prevBtn;
    TextView epubTitle;
    int currentPlaying = 0;
    NotifyingThread initializer;

    NotificationManager notificationManager;

    private Handler mHandler;
    private BottomSheetDialog dialog;

    private List<Track> chapters;

    APIService apiServices = APIUtils.getUserService();

    private class MediaInit extends NotifyingThread {
        @Override
        public void doRun() {
            try {
                System.out.println("Initializing.....");
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        findViewById(R.id.statusBarView).getLayoutParams().height = getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android")
        );
        findViewById(R.id.statusBarView).requestLayout();

//                .setPaddingRelative(0, getResources().getDimensionPixelSize(
//                getResources().getIdentifier("status_bar_height", "dimen", "android")
//        ), 0, 0);

        mHandler = new Handler();

        progress_bar = (SeekBar) findViewById(R.id.progress_bar);
        progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });

        ImageButton backBtn = findViewById(R.id.player_share);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareBook();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        Bundle extras = getIntent().getExtras();

        String id = extras.getString("EXTRA_MESSAGE_ID");

        SharedPreferences sharedPrefs = this.getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
        String accessToken = sharedPrefs.getString("accessToken", "");
        Call<DefaultResponseObj> updateLatestBookRes = apiServices.latestBook("Bearer " + accessToken, id);
        updateLatestBookRes.enqueue(new Callback<DefaultResponseObj>() {
            @Override
            public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
            }

            @Override
            public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                Toast.makeText(PlayerActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });

        Call<DetailReponseObj> callDetail = apiServices.detailBook(id);
        callDetail.enqueue(new Callback<DetailReponseObj>() {
            @Override
            public void onResponse(Call<DetailReponseObj> call, Response<DetailReponseObj> response) {
                BookDetail bookDetail = response.body().getData().get(0);
                AtomicInteger index = new AtomicInteger(0);

                chapters = Arrays.stream(bookDetail.getAudio().toArray())
                        .sorted()
                        .map(link -> new Track(getString(R.string.chapter) + " " + index.incrementAndGet(), bookDetail.getBookTitle(), bookDetail.getAuthor(), (String) link, bookDetail.getBookCover()))
                        .collect(Collectors.toList());

                epubTitle = findViewById(R.id.player_booktitle);
                epubTitle.setText(bookDetail.getBookTitle());

                ImageButton tocButton = findViewById(R.id.player_toc);
                tocButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = new BottomSheetDialog(PlayerActivity.this, R.style.SheetDialog);

                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        View view = getLayoutInflater().inflate(R.layout.epub_toc_dialog, null);

                        dialog.setContentView(view);
                        dialog.show();

                        ListView listTocView = (ListView) dialog.findViewById(R.id.list_toc);

                        List<String> values = new ArrayList<>();

                        for (int i = 0; i < chapters.size(); i++) {
                            values.add(getResources().getString(R.string.chapter) + " " + (i + 1));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(dialog.getContext(),
                                R.layout.chapter_item, R.id.chapter_name, values);
                        listTocView.setAdapter(adapter);

                        listTocView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                                if (currentPlaying != position) {
                                    currentPlaying = position;
                                    CreateNotification.createNotification(PlayerActivity.this, chapters.get(currentPlaying), getPlayPauseIcon(), currentPlaying, chapters.size() - 1);
                                    loadMp3();
                                }
                                dialog.cancel();
                            }
                        });
                    }
                });


                title = findViewById(R.id.listen_chap_title);
                title.setText(chapters.get(0).getTitle());

                author = findViewById(R.id.listen_author);
                author.setText(bookDetail.getAuthor());

                allTime = findViewById(R.id.listen_all_time);

                Picasso.get()
                        .load(bookDetail.getBookCover())
                        .transform(new BlurTransformation(getBaseContext(), 18, 2))
                        .into((ImageView) findViewById(R.id.listen_blur_bg));

                Picasso.get()
                        .load(bookDetail.getBookCover())
                        .into((ImageView) findViewById(R.id.listen_cover));

                ImageButton replayTen = findViewById(R.id.replay_ten);
                replayTen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replaceTenSeconds();
                    }
                });

                ImageButton skipTen = findViewById(R.id.skip_ten);
                skipTen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipTenSeconds();
                    }
                });

                nextBtn = findViewById(R.id.button_next);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextChapter(v);
                    }
                });

                prevBtn = findViewById(R.id.button_previous);
                prevBtn.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.transparentGrayColor), android.graphics.PorterDuff.Mode.SRC_IN);
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevChapter(v);
                    }
                });

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                currentTime = findViewById(R.id.listen_current_time);


                buttonPlayPause = findViewById(R.id.button_play_pause);
                buttonPlayPause.setEnabled(false);
                buttonPlayPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseHandler(view);
                    }
                });
//
//                try {
//                    mediaPlayer.setDataSource(chapters.get(0).getMp3Link());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        progress_bar.setProgress(mp.getCurrentPosition());
                        // TODO Auto-generated method stub
//                        mediaPlayer.stop();
                        if (currentPlaying == chapters.size() - 1) {
//                            mediaPlayer.reset();
                            mediaPlayer.seekTo(0);
                            mediaPlayer.pause();
                            buttonPlayPause.setImageResource(R.drawable.ic_play);
                            CreateNotification.createNotification(PlayerActivity.this, chapters.get(currentPlaying), R.drawable.ic_play, currentPlaying, chapters.size() - 1);
                        } else {
                            nextChapter(nextBtn);
                        }
                    }
                });

                spinKit = (ProgressBar) findViewById(R.id.spin_kit);
                Sprite doubleBounce = new DoubleBounce();
                spinKit.setIndeterminateDrawable(doubleBounce);
                spinKit.setVisibility(View.VISIBLE);

                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        progress_bar.setSecondaryProgress((int) (((double) percent / 100) * (mediaPlayer.getDuration() / 1000)));
                        if (spinKit.getVisibility() == View.VISIBLE) {
                            spinKit.setVisibility(View.GONE);
                        }
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Intent networkErr = new Intent(PlayerActivity.this, NetworkErrorActivity.class);
                        startActivity(networkErr);
                        PlayerActivity.this.finish();
                        return false;
                    }
                });
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        buttonPlayPause.setImageResource(R.drawable.ic_pause);
                        allTime.setText(minisecondToMinute(mediaPlayer.getDuration()));
                        buttonPlayPause.setEnabled(true);
                    }
                });
                CreateNotification.createNotification(PlayerActivity.this, chapters.get(currentPlaying), R.drawable.ic_pause, 0, chapters.size() - 1);
                loadMp3();

            }

            @Override
            public void onFailure(Call<DetailReponseObj> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareBook() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(PlayerActivity.this, R.string.err_location_permission, Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    124);
            return;
        }
        SharedPreferences sharedPrefs = this.getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
        boolean isLogin = sharedPrefs.getBoolean("isLoggedIn", false);
        if (!isLogin) {
            playPauseHandler(buttonPlayPause);
            Intent intent = new Intent(PlayerActivity.this, LoginActivity.class);
            startActivityForResult(intent, 1);
            Toast.makeText(PlayerActivity.this, R.string.logInRequest, Toast.LENGTH_SHORT).show();
        } else {
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location != null) {
                String accessToken = sharedPrefs.getString("accessToken", "");
                Call<DefaultResponseObj> callDetail = apiServices.updateLocation("Bearer " + accessToken, location.getLatitude(), location.getLongitude());
                callDetail.enqueue(new Callback<DefaultResponseObj>() {
                    @Override
                    public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                        System.out.println("Share location " + response.body() + "    " + location);
                        if (response.body() == null || !response.body().getMesssage().equals("success")) {
                            onFailure(call, null);
                            return;
                        }

                        Bundle extras = getIntent().getExtras();
                        String id = extras.getString("EXTRA_MESSAGE_ID");
                        Call<DefaultResponseObj> shareResponse = apiServices.shareBook("Bearer " + accessToken, id);
                        shareResponse.enqueue(new Callback<DefaultResponseObj>() {
                            @Override
                            public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                                System.out.println("Share: " + response.body());
                                if (response.body() == null) {
                                    onFailure(call, null);
                                    return;
                                }
                                Toast.makeText(PlayerActivity.this, R.string.shared_string, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                                Toast.makeText(PlayerActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                        Toast.makeText(PlayerActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void skipTenSeconds() {
        mediaPlayer.pause();
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10_000);
        mediaPlayer.start();
    }

    private void replaceTenSeconds() {
        mediaPlayer.pause();
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10_000);
        mediaPlayer.start();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "mindBook", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void loadMp3() {
        title.setText(chapters.get(currentPlaying).getTitle());
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(chapters.get(currentPlaying).getMp3Link());
        } catch (IOException e) {
            e.printStackTrace();
        }
        spinKit.setVisibility(View.VISIBLE);
        initializer = new MediaInit();


        initializer.addListener(new ThreadCompleteListener() {
            @Override
            public void notifyOfThreadComplete(Thread thread) {
                progress_bar.setMax(mediaPlayer.getDuration() / 1000);
                PlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            progress_bar.setProgress(mCurrentPosition);
                            currentTime.setText(minisecondToMinute(mediaPlayer.getCurrentPosition()));
                        }
                        mHandler.postDelayed(this, 1000);
                    }
                });
            }
        });
        initializer.start();
//        mediaPlayer.start();
//        initializer.start();

    }

    public void nextChapter(View view) {
        if (currentPlaying == 0) {
            prevBtn.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (currentPlaying < chapters.size() - 1) {
            currentPlaying = Math.min(chapters.size() - 1, ++currentPlaying);
            CreateNotification.createNotification(PlayerActivity.this, chapters.get(currentPlaying), getPlayPauseIcon(), currentPlaying, chapters.size() - 1);
            loadMp3();
            if (currentPlaying == chapters.size() - 1) {
                nextBtn.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.transparentGrayColor), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public void prevChapter(View view) {
        if (currentPlaying == chapters.size() - 1) {
            nextBtn.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (currentPlaying > 0) {
            currentPlaying = Math.max(0, --currentPlaying);
            CreateNotification.createNotification(PlayerActivity.this, chapters.get(currentPlaying), getPlayPauseIcon(), currentPlaying, chapters.size() - 1);
            loadMp3();
            if (currentPlaying == 0) {
                prevBtn.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.transparentGrayColor), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public void playPauseHandler(View v) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlayPause.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            buttonPlayPause.setImageResource(R.drawable.ic_pause);
        }
        CreateNotification.createNotification(PlayerActivity.this, chapters.get(currentPlaying), getPlayPauseIcon(), currentPlaying, chapters.size() - 1);

    }

    private int getPlayPauseIcon() {
        if (mediaPlayer.isPlaying()) {
            return R.drawable.ic_pause;
        } else {
            return R.drawable.ic_play;
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action_name");

            switch (action) {
                case CreateNotification.ACTION_PLAY:
                    playPauseHandler(buttonPlayPause);
                    break;
                case CreateNotification.ACTION_PREV:
                    prevChapter(prevBtn);
                    break;
                case CreateNotification.ACTION_NEXT:
                    nextChapter(nextBtn);
                    break;
                case CreateNotification.ACTION_SKIP_TEN:
                    skipTenSeconds();
                    break;
                case CreateNotification.ACTION_REPLAY_TEN:
                    replaceTenSeconds();
                    break;
            }
        }
    };

    private String minisecondToMinute(int millisecond) {
        int mm = millisecond / 60000;
        int ss = (millisecond % 60000) / 1000;
        return String.format("%2s:%2s", mm, ss).replace(' ', '0');
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }
}
