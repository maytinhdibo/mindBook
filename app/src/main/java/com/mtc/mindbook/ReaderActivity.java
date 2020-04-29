package com.mtc.mindbook;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;

public class ReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Bundle extras = getIntent().getExtras();
        String type = extras.getString("EXTRA_MESSAGE_TYPE");
        String id = extras.getString("EXTRA_MESSAGE_ID");

        // Capture the layout's TextView and set the string as its text
//        TextView textView = findViewById(R.id.reader);
        HtmlTextView textView = (HtmlTextView) findViewById(R.id.html_text);

//        textView.setText("Type open is: " + type + ", ID: " + id);
        Toast.makeText(ReaderActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
        try {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(getAssets().open("epublibviewer-help.epub"));
            List<String> titles = book.getMetadata().getTitles();
            textView.setText("book title:" + (titles.isEmpty() ? "book has no title" : titles.get(0)));
            Spine spine = book.getSpine();
            StringBuilder htmlText = new StringBuilder();
            for (int i = 0; i < spine.size(); i++) {
                Resource resource = spine.getResource(i);
                htmlText.append(new String(resource.getData()));
//                textView.append(Html.fromHtml(new String(resource.getData()), Html.FROM_HTML_MODE_COMPACT));

            }

            textView.setHtml(htmlText.toString(), new HtmlResImageGetter(textView.getContext()));
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

    }

}
