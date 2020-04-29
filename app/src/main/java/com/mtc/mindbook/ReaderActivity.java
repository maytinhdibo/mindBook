package com.mtc.mindbook;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;

public class ReaderActivity extends AppCompatActivity {

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Bundle extras = getIntent().getExtras();
        String type = extras.getString("EXTRA_MESSAGE_TYPE");
        String id = extras.getString("EXTRA_MESSAGE_ID");

//        MaterialToolbar topBar = (MaterialToolbar) findViewById(R.id.epub_top_bar);

        // Use webview
        WebView epubContent = (WebView) findViewById(R.id.epub_content);
        epubContent.setBackgroundColor(Color.TRANSPARENT);

        Toast.makeText(ReaderActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
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

}
