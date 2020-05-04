package com.mtc.mindbook;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mtc.mindbook.models.responseObj.Detail;
import com.mtc.mindbook.models.responseObj.DetailReponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {



    @Override
    protected void onStart() {
        APIService detailService = null;
        detailService = APIUtils.getUserService();

        Bundle extras = getIntent().getExtras();

        String id = extras.getString("EXTRA_MESSAGE_ID");

        Call<DetailReponseObj> callDetail = detailService.detailBook(id);
        callDetail.enqueue(new Callback<DetailReponseObj>() {
            @Override
            public void onResponse(Call<DetailReponseObj> call, Response<DetailReponseObj> response) {
                Detail detail = response.body().getData().get(0);

                Toolbar toolbar = findViewById(R.id.epub_top_bar);
                toolbar.setTitle(detail.getBookTitle());
            }

            @Override
            public void onFailure(Call<DetailReponseObj> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });

        super.onStart();

    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Bundle extras = getIntent().getExtras();
        String type = extras.getString("EXTRA_MESSAGE_TYPE");
        String id = extras.getString("EXTRA_MESSAGE_ID");

        Toolbar toolbar = findViewById(R.id.epub_top_bar);
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
        WebView epubContent = (WebView) findViewById(R.id.epub_content);
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

        try {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(getAssets().open("pg730.epub"));
            List<String> titles = book.getMetadata().getTitles();
            Spine spine = book.getSpine();
            StringBuilder htmlText = new StringBuilder();
//            for (int i = 0; i < spine.size(); i++) {
                Resource resource = spine.getResource(2);
                htmlText.append(new String(resource.getData()));

//            }


            // Use webview
            String encodedHtml = Base64.encodeToString(htmlText.toString().getBytes(),
                    Base64.NO_PADDING);
            epubContent.loadData(encodedHtml, "text/html", "base64");
            WebSettings webSettings = epubContent.getSettings();
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            webSettings.setDefaultTextEncodingName("utf-8");
            webSettings.setJavaScriptEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
            finish();
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    BottomNavigationView bottomBar = findViewById(R.id.epub_bottom_bar);
                    int itemIndex = 0;
                    switch (item.getItemId()) {
                        case R.id.prev_chap:
                            itemIndex = 0;
                            Toast.makeText(getApplicationContext(), "Prev Chapter", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.next_chap:
                            itemIndex = 1;
                            Toast.makeText(getApplicationContext(), "Next Chapter", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.epub_love:
                            itemIndex = 2;
                            Toast.makeText(getApplicationContext(), "Love Epub", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.epub_custom:
                            itemIndex = 3;
                            Toast.makeText(getApplicationContext(), "Custom Reader", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    bottomBar.getMenu().getItem(itemIndex).setCheckable(false);

                    return true;
                }
            };

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / getResources().getDisplayMetrics().density;
    }

}
