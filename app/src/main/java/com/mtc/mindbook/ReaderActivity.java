package com.mtc.mindbook;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mtc.mindbook.gestures.OnSwipeTouchListener;
import com.mtc.mindbook.models.playlist.AddlistAdapter;
import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.detail.BookDetail;
import com.mtc.mindbook.models.responseObj.detail.DetailReponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDataResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
import com.mtc.mindbook.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    String id;
    private int currentChapter = 0;
    private Book book;
    private WebView epubContent;
    private LinearLayout toolbar;
    private ImageView arrowPopup;
    private ImageView tocButton;
    private ImageView searchButton;
    private boolean isSearching = false;
    EditText searchText;

    private int fontSize;
    private int MIN_FONT_SIZE = 20;
    private String backgroundColor;
    private int fontFamily;
    private String fontFamilyString;
    private String fontColor;
    SharedPreferences sharedPrefs = null;


    private BottomSheetDialog dialog;
    private BroadcastReceiver onDoneDownload;
    private LocationManager locationManager;
    private Integer searchResultCount = 0;
    private Integer searchIndexInPage = 0;
    //    private Boolean isSearchDone;
    Context context = this;
    APIService apiServices = APIUtils.getUserService();

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

        Bundle extras = getIntent().getExtras();

        id = extras.getString("EXTRA_MESSAGE_ID");

        sharedPrefs = this.getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
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
                Toast.makeText(ReaderActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });

        Call<DetailReponseObj> callDetail = apiServices.detailBook(id);
        callDetail.enqueue(new Callback<DetailReponseObj>() {
            @Override
            public void onResponse(Call<DetailReponseObj> call, Response<DetailReponseObj> response) {

                BookDetail bookDetail = response.body().getData().get(0);

                TextView title = findViewById(R.id.epub_title);
                title.setText(bookDetail.getBookTitle());

                String epubLink = bookDetail.getBookEpub();
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

                tocButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = new BottomSheetDialog(ReaderActivity.this, R.style.SheetDialog);

                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        View view = getLayoutInflater().inflate(R.layout.epub_toc_dialog, null);

                        dialog.setContentView(view);
                        dialog.show();

                        ListView listTocView = dialog.findViewById(R.id.list_toc);

                        List<String> values = new ArrayList<>();

                        for (int i = 0; i < book.getTableOfContents().getTocReferences().size(); i++) {
                            values.add(book.getTableOfContents().getTocReferences().get(i).getTitle());
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(dialog.getContext(),
                                R.layout.chapter_item, R.id.chapter_name, values);

                        listTocView.setAdapter(adapter);

                        listTocView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                                    long arg3) {
                                String resourceID = book.getTableOfContents().getTocReferences().get(position).getResourceId();
                                currentChapter = book.getSpine().findFirstResourceById(resourceID);
                                try {
                                    loadChapter();
                                    dialog.cancel();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                    }
                });

                LinearLayout searchBar = findViewById(R.id.epub_search_bar);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isSearching) {
                            isSearching = true;
                            searchButton.setImageResource(R.drawable.ic_close);

                            searchBar.setVisibility(View.VISIBLE);
                            searchBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).alpha(1).start();
                        } else {
                            isSearching = false;
                            searchButton.setImageResource(R.drawable.ic_search);
                            searchBar.animate().translationY(-(searchBar.getTop() - searchBar.getBottom())).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    searchBar.setVisibility(View.GONE);
                                }
                            }).alpha(0).start();
                            epubContent.clearMatches();
                            searchIndexInPage = 0;
                            searchResultCount = 0;
                        }
                    }
                });

                ImageButton searchNextBtn = findViewById(R.id.epub_search_next);
                searchNextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (searchResultCount == 0) {
                            try {
                                loadPrevChapter();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            searchIndexInPage--;
                            epubContent.findNext(false);
                        }
                    }
                });

                ImageButton searchBackBtn = findViewById(R.id.epub_search_back);
                searchBackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (searchResultCount == 0) {
                            try {
                                loadNextChapter();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            searchIndexInPage++;
                            epubContent.findNext(true);
                        }
                    }
                });

                searchText = findViewById(R.id.search_keyword);
                searchText.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            search();
                            return true;
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onFailure(Call<DetailReponseObj> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void search() {
//        Spine spine = book.getSpine();
//        epubContent.setVisibility(View.GONE);
//        isSearchDone = false;
        searchIndexInPage = 0;
        epubContent.setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                if (isDoneCounting) {
                    if (searchIndexInPage >= 0 && searchIndexInPage < numberOfMatches && activeMatchOrdinal != searchIndexInPage) {
                        searchIndexInPage = activeMatchOrdinal;
                    }
                    if (searchIndexInPage == numberOfMatches && numberOfMatches != 0) {
                        try {
                            searchIndexInPage = 0;
                            loadNextChapter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (searchIndexInPage == -1) {
                        try {
                            searchIndexInPage = 0;
                            loadPrevChapter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    searchResultCount = numberOfMatches;
//                    searchIndexInPage = numberOfMatches > 0 ? activeMatchOrdinal + 1: 0;
                    reloadSearchText();
                }
            }
        });
        epubContent.findAllAsync(searchText.getText().toString());


//        for (int i = 0; i < spine.size(); i++) {
//            StringBuilder htmlText = new StringBuilder();
//            Resource resource = spine.getResource(i);
//            try {
//                htmlText.append(new String(resource.getData()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            epubContent.loadDataWithBaseURL("file:///android_asset/", htmlText.toString(), "text/html", "UTF-8", "");
//            epubContent.findAllAsync(searchText.getText().toString());
//        }

//        try {
//            loadChapter();
//            epubContent.setVisibility(View.VISIBLE);
//            epubContent.findAllAsync(searchText.getText().toString());
//            isSearchDone = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void reloadSearchText() {
        TextView resultText = findViewById(R.id.epub_search_result_text);
        StringBuilder newText = new StringBuilder();
        newText.append("Hiển thị kết quả ")
                .append(Math.min(searchIndexInPage + 1, searchResultCount))
                .append(" trên ")
                .append(searchResultCount)
                .append(" kết quả.");
        resultText.setText(newText);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        arrowPopup = findViewById(R.id.arrow);

        tocButton = findViewById(R.id.toc_btn);

        searchButton = findViewById(R.id.epub_search_btn);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setStatusBarColor(R.color.ocean_color);


        Bundle extras = getIntent().getExtras();
        String type = extras.getString("EXTRA_MESSAGE_TYPE");
        String id = extras.getString("EXTRA_MESSAGE_ID");

        toolbar = findViewById(R.id.epub_top_bar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);


//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        BottomNavigationView bottomBar = findViewById(R.id.epub_bottom_bar);
        bottomBar.setOnNavigationItemSelectedListener(navListener);
        bottomBar.getMenu().getItem(0).setCheckable(false);


        // Use webview
        epubContent = findViewById(R.id.epub_content);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
                Log.d("TEST", "shouldOverrideUrlLoading: " + url.getUrl());
                return true;
            }

        };

        epubContent.setWebViewClient(webViewClient);

        epubContent.setBackgroundColor(Color.TRANSPARENT);
        epubContent.setOnTouchListener(new OnSwipeTouchListener(ReaderActivity.this) {

            @Override
            public void onSwipeRight() {
                try {
                    loadPrevChapter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSwipeLeft() {
                try {
                    loadNextChapter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClick() {
                if (toolbar.isShown()) {
                    toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            toolbar.setVisibility(View.GONE);
                        }
                    }).alpha(0).start();
                    findViewById(R.id.epub_bottom_bar).setVisibility(View.GONE);

                } else {
                    toolbar.setVisibility(View.VISIBLE);
                    findViewById(R.id.epub_bottom_bar).setVisibility(View.VISIBLE);
                    toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).alpha(1).start();
                }

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    private boolean isAnimating = false;

    private void displayArrow(boolean next) {

        if (next) {
            arrowPopup.setRotationY(180f);
        } else {
            arrowPopup.setRotationY(0f);
        }

        isAnimating = true;
        arrowPopup.getBackground().setAlpha(255);

        arrowPopup.setVisibility(View.VISIBLE);

        arrowPopup.animate().alpha(1.0f).setStartDelay(0)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                arrowPopup.animate().alpha(0.0f).setStartDelay(750)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                if (!isAnimating) {
                                                    super.onAnimationEnd(animation);
                                                    arrowPopup.setVisibility(View.GONE);
                                                    isAnimating = false;
                                                }
                                            }
                                        });
                            }
                        });
    }

    private void loadPrevChapter() throws IOException {
        if (currentChapter == book.getSpine().size() - 1) {
            changeMenuIconTint(R.id.next_chap, R.color.colorPrimary);
        }
        if (currentChapter > 0) {
            currentChapter = Math.max(0, --currentChapter);
            loadChapter();
            if (currentChapter == 0) {
                changeMenuIconTint(R.id.prev_chap, R.color.middleColor);
            }
            displayArrow(false);
        }
    }

    private void loadNextChapter() throws IOException {
        if (currentChapter == 0) {
            changeMenuIconTint(R.id.prev_chap, R.color.colorPrimary);
        }
        if (currentChapter < book.getSpine().size() - 1) {
            currentChapter = Math.min(book.getSpine().size() - 1, ++currentChapter);
            loadChapter();
            if (currentChapter == book.getSpine().size() - 1) {
                changeMenuIconTint(R.id.next_chap, R.color.middleColor);
            }
            displayArrow(true);
        }
    }

    private void changeMenuIconTint(int itemId, int colorId) {
        BottomNavigationView itemView = findViewById(R.id.epub_bottom_bar);
        MenuItem item = itemView.getMenu().findItem(itemId);
        Drawable icon = item.getIcon();
        icon = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(icon, ContextCompat.getColor(this, colorId));
        item.setIcon(icon);
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
                                loadPrevChapter();
                                break;
                            case R.id.next_chap:
                                itemIndex = 1;
                                loadNextChapter();
                                break;
                            case R.id.epub_share:
                                shareEpub();
                                break;
                            case R.id.epub_love:
                                itemIndex = 2;
                                loadAddlist();
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
        dialog = new BottomSheetDialog(ReaderActivity.this, R.style.SheetDialog);

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

    private void shareEpub() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ReaderActivity.this, R.string.err_location_permission, Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    124);
            return;
        }
        SharedPreferences sharedPrefs = this.getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
        boolean isLogin = sharedPrefs.getBoolean("isLoggedIn", false);
        if (!isLogin) {
            Intent intent = new Intent(ReaderActivity.this, LoginActivity.class);
            startActivityForResult(intent, 1);
            Toast.makeText(ReaderActivity.this, R.string.logInRequest, Toast.LENGTH_SHORT).show();
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
                                Toast.makeText(ReaderActivity.this, R.string.shared_string, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                                Toast.makeText(ReaderActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                        Toast.makeText(ReaderActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    List<PlaylistDataResponseObj> addlist = null;

    private void loadAddlist() {
        boolean isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String accessToken = sharedPrefs.getString("accessToken", "");
            Call<PlaylistResponseObj> getPlaylists = apiServices.getUserPlaylistList("Bearer " + accessToken);
            getPlaylists.enqueue(new Callback<PlaylistResponseObj>() {
                @Override
                public void onResponse(Call<PlaylistResponseObj> call, Response<PlaylistResponseObj> response) {
                    addlist = response.body().getData();
                    Dialog addlistDialog = new Dialog(context);
                    addlistDialog.setContentView(R.layout.add_book_to_playlist_dialog);
                    addlistDialog.show();
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);

                    addlistDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                    RecyclerView addlistView = addlistDialog.findViewById(R.id.addlist_playlist);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    addlistView.setLayoutManager(layoutManager);

                    AddlistAdapter addlistAdapter = new AddlistAdapter(addlist, context, id, addlistDialog);
                    addlistView.setAdapter(addlistAdapter);

                    ImageView addToNewPlaylist = addlistDialog.findViewById(R.id.addlist_btn);
                    EditText newPlaylistName = addlistDialog.findViewById(R.id.addlist_playlist_name);
                    addToNewPlaylist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = newPlaylistName.getText().toString();
                            if (name == null || name.equals("") || name.equals(" ")) {
                                Toast.makeText(context, "Tên playlist không được để trống", Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.addBookToNewPlaylist(context, name, id);
                                addlistDialog.dismiss();
                            }
                        }
                    });

                    Button cancel = addlistDialog.findViewById(R.id.addlist_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addlistDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(Call<PlaylistResponseObj> call, Throwable t) {
                    Log.d(",...", "onResponse: " + t.getMessage());
                }
            });
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivityForResult(intent, 1);
            Toast.makeText(context, R.string.logInRequest, Toast.LENGTH_SHORT).show();
        }
    }
    private void initConfigBottomValue() {

        // Set current Config
        TextView currentFont = dialog.findViewById(R.id.epub_current_font);
        currentFont.setText(fontFamilyString);
        Typeface typeface = getCurrentTypeface();
        currentFont.setTypeface(typeface);

        SharedPreferences userSharedPrefs = getSharedPreferences("userDataPrefs", MODE_PRIVATE);
        boolean isNightTheme = userSharedPrefs.getBoolean("isNightTheme", true);
        if (isNightTheme) {
            ImageView lightBtn = dialog.findViewById(R.id.color_changeable);
            lightBtn.setColorFilter(ContextCompat.getColor(ReaderActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }


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
        dialog = new BottomSheetDialog(ReaderActivity.this, R.style.SheetDialog);
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
