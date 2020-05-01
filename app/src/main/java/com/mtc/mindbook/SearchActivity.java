package com.mtc.mindbook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.responseObj.Detail;
import com.mtc.mindbook.models.responseObj.DetailReponseObj;
import com.mtc.mindbook.models.responseObj.Search;
import com.mtc.mindbook.models.responseObj.SearchReponseObj;
import com.mtc.mindbook.models.search.SearchItem;
import com.mtc.mindbook.models.search.SearchViewAdapter;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import java.util.ArrayList;
import java.util.Arrays;
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


        Bundle tagValue = getIntent().getExtras();

        if (tagValue != null) {
            searchInput.setText(tagValue.getString("EXTRA_TAG"));
            searchInput.setSelection(searchInput.getText().length());
        }

        ImageButton voiceBtn = findViewById(R.id.voiceSearchBtn);

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        SearchItem[] todoItems = {
                new SearchItem(1, "https://a.wattpad.com/cover/155025710-288-k448920.jpg", "Tuyển tập Ngô Tất Tố", "Ngô Tất Tố", 4),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg", "Mắt biếc", "Hà Lan", 4.3),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg", "Những con phố dài", "Nguyên Thảo", 4.3),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg", "Mảnh đời bất tận", "Hà Lan", 1.3),
                new SearchItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg", "Mắt biếc", "Hà Lan", 4.3),
        };

        final List<SearchItem> listItem = new ArrayList<>(Arrays.asList(todoItems));


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
        APIService searchService = null;
        searchService = APIUtils.getUserService();
        Call<SearchReponseObj> callDetail = searchService.search(searchInput.getText().toString());
        callDetail.enqueue(new Callback<SearchReponseObj>() {

            @Override
            public void onResponse(Call<SearchReponseObj> call, Response<SearchReponseObj> response) {
                List<Search> searchs = response.body().getData();
                final SearchViewAdapter adapter = new SearchViewAdapter(searchs);
                searchResult.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<SearchReponseObj> call, Throwable t) {
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
                }
                break;
            }
        }
    }
}
