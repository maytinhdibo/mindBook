package com.mtc.mindbook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.AppUtil;
import com.folioreader.util.ReadLocatorListener;
import com.mtc.mindbook.models.EntryItem;
import com.mtc.mindbook.models.RecyclerViewAdapter;
import com.mtc.mindbook.models.search.SearchItem;
import com.mtc.mindbook.models.search.SearchViewAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText searchInput = findViewById(R.id.searchText);
        searchInput.requestFocus();


        ImageButton voiceBtn = findViewById(R.id.voiceSearchBtn);

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        SearchItem[] todoItems = {
                new SearchItem(1, "https://a.wattpad.com/cover/155025710-288-k448920.jpg", "Tuyển tập Ngô Tất Tố", "Ngô Tất Tố", 4),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg","Mắt biếc", "Hà Lan", 4.3),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg","Những con phố dài", "Nguyên Thảo", 4.3),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg","Mảnh đời bất tận", "Hà Lan", 1.3),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg","Mắt biếc", "Hà Lan", 4.3),
        };

        final List<SearchItem> listItem =  new ArrayList<>(Arrays.asList(todoItems));

        final SearchViewAdapter adapter = new SearchViewAdapter(listItem);

        // layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());

        // for you
        RecyclerView listViewForYou = findViewById(R.id.search_result);
        listViewForYou.setLayoutManager(layoutManager);
        listViewForYou.setAdapter(adapter);

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.enter_search));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Not support voice input!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EditText searchInput = findViewById(R.id.searchText);
                    searchInput.setText(result.get(0));
                    searchInput.setSelection(searchInput.getText().length());
                }
                break;
            }
        }
    }
}
