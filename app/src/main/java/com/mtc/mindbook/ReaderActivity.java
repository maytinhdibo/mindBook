package com.mtc.mindbook;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.AppUtil;
import com.folioreader.util.ReadLocatorListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReaderActivity extends AppCompatActivity implements ReadLocatorListener, FolioReader.OnClosedListener {

    private static final String LOG_TAG = ReaderActivity.class.getSimpleName();
    private FolioReader folioReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Bundle extras = getIntent().getExtras();
        String type = extras.getString("EXTRA_MESSAGE_TYPE");
        String id = extras.getString("EXTRA_MESSAGE_ID");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.reader);
        textView.setText("Type open is: " + type + ", ID: "+id);

        folioReader = FolioReader.get().setReadLocatorListener(this)
                .setOnClosedListener(this);

        ReadLocator readLocator = getLastReadLocator();
        Config config = AppUtil.getSavedConfig(getApplicationContext());
        if (config == null) {
            config = new Config();
        }

        config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

        folioReader.setReadLocator(readLocator);
        folioReader.setConfig(config, true).openBook(R.raw.accessible_epub_3);



    }

    private ReadLocator getLastReadLocator() {
        String jsonString = loadAssetTextAsString("LastReadLocators/last_read_locator_1.json");
        return ReadLocator.fromJson(jsonString);
    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        Log.i(LOG_TAG, "-> saveReadLocator -> " + readLocator.toJson());
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("ReaderActivity", "Error opening asset " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("ReaderActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FolioReader.clear();
    }

    @Override
    public void onFolioReaderClosed() {
        Log.v(LOG_TAG, "-> onFolioReaderClosed");
    }
}
