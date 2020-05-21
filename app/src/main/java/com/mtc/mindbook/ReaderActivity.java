package com.mtc.mindbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mtc.mindbook.models.responseObj.detail.Detail;
import com.mtc.mindbook.models.responseObj.detail.DetailReponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    private int currentChapter = 1;
    private Book book;
    private WebView epubContent;
    private Toolbar toolbar;


    private int fontSize = 30;
    private int MIN_FONT_SIZE = 20;
    private String backgroundColor = "#fff";
    private String fontFamily = "Open Sans";
    private String fontColor = "#000";


    private BroadcastReceiver onDoneDownload;

    @Override
    protected void onStart() {
        boolean isReadPermission = checkPermission();
        if (!isReadPermission) {
            ActivityCompat.requestPermissions(ReaderActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    21);
        } else {
            loadEpub();
        }

        super.onStart();

    }

    private void loadEpub() {
        APIService detailService = null;
        detailService = APIUtils.getUserService();

        Bundle extras = getIntent().getExtras();

        String id = extras.getString("EXTRA_MESSAGE_ID");

        Call<DetailReponseObj> callDetail = detailService.detailBook(id);
        callDetail.enqueue(new Callback<DetailReponseObj>() {
            @Override
            public void onResponse(Call<DetailReponseObj> call, Response<DetailReponseObj> response) {

                Detail detail = response.body().getData().get(0);

                toolbar.setTitle(detail.getBookTitle());

                String epubLink = detail.getBookEpub();
                String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
                Log.d("Storage", "" + storagePath);
                File f = new File(storagePath);
                if (!f.exists()) {
                    f.mkdirs();
                }

                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);


                Uri uri = Uri.parse(epubLink);
                String epubFilePath = storagePath + File.separator + uri.getLastPathSegment();
                File epubFile = new File(epubFilePath);

                onDoneDownload = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            EpubReader epubReader = new EpubReader();
                            book = epubReader.readEpub(new FileInputStream(epubFile));
                            loadChapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    }
                };

                registerReceiver(onDoneDownload, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                if (!epubFile.exists()) {

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI
                    )
                            .setAllowedOverRoaming(false)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, uri.getLastPathSegment());
                    Long referese = dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading ...", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        EpubReader epubReader = new EpubReader();
                        book = epubReader.readEpub(new FileInputStream(epubFile));
                        loadChapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(Call<DetailReponseObj> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);


        Bundle extras = getIntent().getExtras();
        String type = extras.getString("EXTRA_MESSAGE_TYPE");
        String id = extras.getString("EXTRA_MESSAGE_ID");

        toolbar = findViewById(R.id.epub_top_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BottomNavigationView bottomBar = findViewById(R.id.epub_bottom_bar);
        bottomBar.setOnNavigationItemSelectedListener(navListener);
        bottomBar.getMenu().getItem(0).setCheckable(false);


        // Use webview
        epubContent = (WebView) findViewById(R.id.epub_content);
        epubContent.setBackgroundColor(Color.TRANSPARENT);
        epubContent.setOnTouchListener(new OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 200;
            private static final int MAX_CLICK_DISTANCE = 15;
            private long pressStartTime;
            private float pressedX;
            private float pressedY;
            private boolean stayedWithinClickDistance;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        pressStartTime = System.currentTimeMillis();
                        pressedX = e.getX();
                        pressedY = e.getY();
                        stayedWithinClickDistance = true;
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (stayedWithinClickDistance && distance(pressedX, pressedY, e.getX(), e.getY()) > MAX_CLICK_DISTANCE) {
                            stayedWithinClickDistance = false;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
                            if (toolbar.isShown()) {
                                ActionBar x = getSupportActionBar();
                                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        x.hide();
                                    }
                                }).alpha(0).start();

                            } else {
                                getSupportActionBar().show();
                                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).alpha(1).start();
                            }

                        }
                    }
                }
                return false;
            }
        });

        WebSettings webSettings = epubContent.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 21: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadEpub();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.epub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.epub_search) {
            Toast.makeText(getApplicationContext(), "You click search", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.add_bookmark) {
            Toast.makeText(getApplicationContext(), "You click add bookmark", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.epub_help) {
            Toast.makeText(getApplicationContext(), "You click help", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.epub_feedback) {
            Toast.makeText(getApplicationContext(), "You click feedback", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(ReaderActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ReaderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void loadChapter() throws IOException {
        Spine spine = book.getSpine();
        StringBuilder htmlText = new StringBuilder();
        Resource resource = spine.getResource(currentChapter);
        htmlText.append(new String(resource.getData()));
        htmlText.append("<style type=\"text/css\">" +
                "* { " +
                "font-size: " + fontSize + "px !important;" +
                "font-family: " + fontFamily + ";" +
                "color: " + fontColor + ";" +
                "} body {margin:75px 20px 200px 20px;} </style>");
        String encodedHtml = Base64.encodeToString(htmlText.toString().getBytes(),
                Base64.NO_PADDING);
        epubContent.loadData(encodedHtml, "text/html", "base64");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    BottomNavigationView bottomBar = findViewById(R.id.epub_bottom_bar);
                    int itemIndex = 0;
                    try {
                        switch (item.getItemId()) {
                            case R.id.prev_chap:
                                itemIndex = 0;
                                if (currentChapter <= 0) {
                                    Toast.makeText(getApplicationContext(), "This is the first chapter", Toast.LENGTH_SHORT).show();
                                } else {
                                    currentChapter = Math.max(0, --currentChapter);
                                    loadChapter();
                                }
                                break;
                            case R.id.next_chap:
                                itemIndex = 1;
                                if (currentChapter >= book.getSpine().size() - 1) {
                                    Toast.makeText(getApplicationContext(), "This is the last chapter", Toast.LENGTH_SHORT).show();
                                } else {
                                    currentChapter = Math.min(book.getSpine().size() - 1, ++currentChapter);
                                    loadChapter();
                                }
                                break;
                            case R.id.epub_love:
                                itemIndex = 2;
                                Toast.makeText(getApplicationContext(), "Love Epub", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.epub_custom:
                                itemIndex = 3;
                                toggleDialogBottom();
                                break;
                        }
                        bottomBar.getMenu().getItem(itemIndex).setCheckable(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                        finish();
                    }
                    return true;
                }
            };

    @SuppressLint("SetTextI18n")
    private void toggleDialogBottom() {
        BottomSheetDialog dialog = new BottomSheetDialog(ReaderActivity.this);
        View view = getLayoutInflater().inflate(R.layout.epub_custom_dialog, null);
        dialog.setContentView(view);
        dialog.show();

        TextView currentFontSize = dialog.findViewById(R.id.epub_current_font_size);
        currentFontSize.setText(fontSize + "px");
        SeekBar fontSizeSeekBar = view.findViewById(R.id.font_size_seekbar);
        fontSizeSeekBar.setProgress(fontSize - MIN_FONT_SIZE);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.getProgress();
                fontSize = seekBar.getProgress() + MIN_FONT_SIZE;
                currentFontSize.setText(fontSize + "px");
                Log.d("test", "onStopTrackingTouch: " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    loadChapter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ((TextView) dialog.findViewById(R.id.epub_current_font)).setText(fontFamily);
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(onDoneDownload);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
