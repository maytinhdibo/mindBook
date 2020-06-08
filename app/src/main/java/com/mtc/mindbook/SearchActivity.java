package com.mtc.mindbook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.responseObj.search.SearchItem;
import com.mtc.mindbook.models.responseObj.search.SearchResponseObj;
import com.mtc.mindbook.models.search.SearchViewAdapter;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    EditText searchInput;
    RecyclerView searchResult;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchText);
        searchInput.requestFocus();

        searchInput.setOnKeyListener(new View.OnKeyListener() {
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

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        findViewById(R.id.statusBarView).setPaddingRelative(0, getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android")
        ), 0, 0);

        Bundle tagValue = getIntent().getExtras();

        if (tagValue != null) {
            searchInput.setText(tagValue.getString("EXTRA_TAG"));
            searchInput.setSelection(searchInput.getText().length());
            search();
        }

        ImageButton voiceBtn = findViewById(R.id.voiceSearchBtn);

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        // layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());

        // for you
        searchResult = findViewById(R.id.search_result);
        searchResult.setLayoutManager(layoutManager);
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

    public void search() {
        ProgressBar loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        APIService searchService = null;
        searchService = APIUtils.getUserService();
        Call<SearchResponseObj> callDetail = searchService.search(searchInput.getText().toString());
        callDetail.enqueue(new Callback<SearchResponseObj>() {
            @Override
            public void onResponse(Call<SearchResponseObj> call, Response<SearchResponseObj> response) {
                if(response.body()== null) {
                    onFailure(call, null);
                    return;
                }
                List<SearchItem> searchItems = response.body().getData();
                if(searchItems.size()==0){
                    findViewById(R.id.null_result).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.null_result).setVisibility(View.GONE);
                }
                final SearchViewAdapter adapter = new SearchViewAdapter(searchItems);
                loading.setVisibility(View.GONE);
                searchResult.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<SearchResponseObj> call, Throwable t) {
                Toast.makeText(SearchActivity.this, R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchInput.setText(result.get(0));
                    searchInput.setSelection(searchInput.getText().length());
                    search();
                }
                break;
            }
        }
    }
}
