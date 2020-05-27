package com.mtc.mindbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mtc.mindbook.models.responseObj.detail.Detail;
import com.mtc.mindbook.models.responseObj.detail.DetailReponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

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


    private int fontSize;
    private int MIN_FONT_SIZE = 20;
    private String backgroundColor;
    private int fontFamily;
    private String fontFamilyString;
    private String fontColor;
    SharedPreferences sharedPrefs = null;


    private BottomSheetDialog dialog;

    private BroadcastReceiver onDoneDownload;

    @Override
    protected void onStart() {
        boolean isReadPermission = checkPermission();
        sharedPrefs = getSharedPreferences("epubCustomPrefs", MODE_PRIVATE);
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

                fontSize = sharedPrefs.getInt("fontSize", 30);
                backgroundColor = sharedPrefs.getString("backgroundColor", "EAE4E4");
                fontFamily = sharedPrefs.getInt("fontFamily", R.id.font_literata);
                fontFamilyString = sharedPrefs.getString("fontFamilyString", "Literata");
                fontColor = sharedPrefs.getString("fontColor", "000000");

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

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
                Log.d("TESST", "shouldOverrideUrlLoading: " + url.getUrl());
                return true;
            }

        };
        epubContent.setWebViewClient(webViewClient);

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
        int intColor = Integer.parseInt(fontColor, 16);
        int intBackgroundColor = Integer.parseInt(backgroundColor, 16);
        htmlText.append("<style type=\"text/css\">" +
                "@font-face {" +
                "font-family:" + fontFamilyString + ";" +
                "src: url(\"" + getCurrentFontInfo() + "\")" +
                "}" +
                "* { " +
                "font-family:" + fontFamilyString + ";" +
                "font-size: " + fontSize + "px !important;" +
                "color: rgb(" + Color.red(intColor) + ", " + Color.green(intColor) + ", " + Color.blue(intColor) + ");" +
                "} " +
                "body {" +
                "margin:25px 20px 20px 20px;" +
                "background-color: rgb(" + Color.red(intBackgroundColor) + ", " + Color.green(intBackgroundColor) + ", " + Color.blue(intBackgroundColor) + ");" +
                "} </style>");
//        String encodedHtml = Base64.encodeToString(htmlText.toString().getBytes(),
//                Base64.NO_PADDING);
        epubContent.loadDataWithBaseURL("file:///android_asset/", htmlText.toString(), "text/html", "UTF-8", "");
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
        // Init bottomSheet
        dialog = new BottomSheetDialog(ReaderActivity.this);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        View view = getLayoutInflater().inflate(R.layout.epub_custom_dialog, null);
        dialog.setContentView(view);
        dialog.show();

        // Setup Listener
        setupUseTemplate();
        View fontView = dialog.findViewById(R.id.font_family);
        fontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFontSelector();
            }
        });

        View textColorView = dialog.findViewById(R.id.text_color);
        textColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleColor("Select text color", fontColor, textColorView.getId());
            }
        });

        View backgroundColorView = dialog.findViewById(R.id.background_color);
        backgroundColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleColor("Select background color", backgroundColor, backgroundColorView.getId());
            }
        });

        initConfigBottomValue();
    }

    private void initConfigBottomValue() {
        // Set current Config
        TextView currentFont = dialog.findViewById(R.id.epub_current_font);
        currentFont.setText(fontFamilyString);
        Typeface typeface = getCurrentTypeface();
        currentFont.setTypeface(typeface);

        TextView currentFontSize = dialog.findViewById(R.id.epub_current_font_size);
        currentFontSize.setText(fontSize + "px");
        SeekBar fontSizeSeekBar = dialog.findViewById(R.id.font_size_seekbar);
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
                    putToPrefs("fontSize", Integer.valueOf(fontSize));
                    loadChapter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView currentTextColor = dialog.findViewById(R.id.current_text_color);
        currentTextColor.setColorFilter(Color.parseColor("#" + fontColor));

        ImageView currentBackgroundColor = dialog.findViewById(R.id.current_background_color);
        currentBackgroundColor.setColorFilter(Color.parseColor('#' + backgroundColor));
    }

    private void setupUseTemplate() {
        ViewGroup listTemplates = dialog.findViewById(R.id.list_templates);

        for (int i = 0; i < listTemplates.getChildCount(); i++) {
            View template = listTemplates.getChildAt(i);
            template.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.dark_template:
                            fontColor = "d1d1d1";
                            backgroundColor = "363636";
                            break;
                        case R.id.light_template:
                            backgroundColor = "ffffff";
                            fontColor = "292929";
                            break;
                        case R.id.paper_template:
                            backgroundColor = "EAE4E4";
                            fontColor = "000000";
                            break;
                        case R.id.ocean_template:
                            backgroundColor = "DBE1F1";
                            fontColor = "000000";
                            break;
                    }
                    try {
                        loadChapter();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    initConfigBottomValue();
                    putToPrefs("fontColor", fontColor);
                    putToPrefs("backgroundColor", backgroundColor);
                }
            });
        }
    }

    private void toggleFontSelector() {
//        dialog.hide();
        dialog.cancel();
        dialog = new BottomSheetDialog(ReaderActivity.this);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        View view = getLayoutInflater().inflate(R.layout.font_selector, null);
        dialog.setContentView(view);
        dialog.show();

        View backBtn = dialog.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                toggleDialogBottom();
            }
        });

        ViewGroup listFont = dialog.findViewById(R.id.font_list);

        for (int ind = 0; ind < listFont.getChildCount(); ind++) {
            View font = listFont.getChildAt(ind);
            font.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fontFamily = v.getId();
                    putToPrefs("fontFamily", fontFamily);
                    fontFamilyString = ((TextView) ((ViewGroup) font).getChildAt(1)).getText().toString();
                    putToPrefs("fontFamilyString", fontFamilyString);
                    try {
                        loadChapter();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                }
            });
        }
        setSelectedFont();
    }

    private void setSelectedFont() {
        ViewGroup listFont = dialog.findViewById(R.id.font_list);

        for (int ind = 0; ind < listFont.getChildCount(); ind++) {
            View font = listFont.getChildAt(ind);
            if (font.getId() == fontFamily) {
                ((ViewGroup) font).getChildAt(0).setVisibility(View.VISIBLE);
            } else {
                ((ViewGroup) font).getChildAt(0).setVisibility(View.INVISIBLE);
            }
        }
    }

    private String getCurrentFontInfo() {
        String ret = "font/";
        switch (fontFamily) {
            case R.id.font_literata:
                ret += "literata.ttf";
                break;
            case R.id.font_merriweather:
                ret += "merriweather.ttf";
                break;
            case R.id.font_sans:
                ret += "opensans.ttf";
                break;
            case R.id.font_volkhov:
                ret += "volkhov.ttf";
                break;
        }
        return ret;
    }

    private Typeface getCurrentTypeface() {
        Typeface ret;
        switch (fontFamily) {
            case R.id.font_literata:
                ret = ResourcesCompat.getFont(ReaderActivity.this, R.font.literata);
                break;
            case R.id.font_merriweather:
                ret = ResourcesCompat.getFont(ReaderActivity.this, R.font.merriweather);
                break;
            case R.id.font_sans:
                ret = ResourcesCompat.getFont(ReaderActivity.this, R.font.opensans);
                break;
            case R.id.font_volkhov:
                ret = ResourcesCompat.getFont(ReaderActivity.this, R.font.volkhov);
                break;
            default:
                ret = ResourcesCompat.getFont(ReaderActivity.this, R.font.literata);
        }
        return ret;
    }

    private void toggleColor(String title, String initColor, int type) {
        String initColorWithAlpha = "#ff" + initColor;
        ColorPickerDialogBuilder
                .with(ReaderActivity.this)
                .setTitle(title)
                .showAlphaSlider(false)
                .initialColor(Color.parseColor(initColorWithAlpha))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        String color = Integer.toHexString(selectedColor).substring(2);
                        switch (type) {
                            case R.id.text_color:
                                System.out.println(Integer.toHexString(selectedColor));
                                fontColor = color;
                                putToPrefs("fontColor", fontColor);
                                break;
                            case R.id.background_color:
                                backgroundColor = color;
                                putToPrefs("backgroundColor", backgroundColor);
                                break;
                        }
                        try {
                            loadChapter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        initConfigBottomValue();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (type) {
                            case R.id.text_color:
                                fontColor = initColor;
                                putToPrefs("fontColor", fontColor);
                                break;
                            case R.id.background_color:
                                backgroundColor = initColor;
                                putToPrefs("backgroundColor", backgroundColor);
                                break;
                        }
                        try {
                            loadChapter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .build()
                .show();
    }

    private <T> void putToPrefs(String key, T value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        editor.apply();
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
