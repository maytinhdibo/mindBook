package com.mtc.mindbook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.responseObj.BookRateResponseObj;
import com.mtc.mindbook.models.responseObj.DetailReponseObj;
import com.mtc.mindbook.models.responseObj.Detail;
import com.mtc.mindbook.models.responseObj.user.UserResponseObj;
import com.mtc.mindbook.models.review.RecyclerReviewAdapter;
import com.mtc.mindbook.models.review.ReviewItem;
import com.mtc.mindbook.models.tag.TagAdapter;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_TYPE = "none";
    public static final String EXTRA_MESSAGE_ID = "none";
    private APIService detailService = null;
    SharedPreferences sharedPrefs = null;
    String id;

    @Override
    protected void onStart() {

        sharedPrefs = getBaseContext().getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);

        detailService = APIUtils.getUserService();

        Bundle tagValue = getIntent().getExtras();

        id = tagValue.getString("EXTRA_BOOK_ID") != null ? tagValue.getString("EXTRA_BOOK_ID") : "1";

        Call<DetailReponseObj> callDetail = detailService.detailBook(id);
        callDetail.enqueue(new Callback<DetailReponseObj>() {
            @Override
            public void onResponse(Call<DetailReponseObj> call, Response<DetailReponseObj> response) {
                Detail detail = response.body().getData().get(0);
                // Render data
                // Render tag
                final List<String> listItem = detail.getCategories();
                final TagAdapter tagAdapter = new TagAdapter(listItem);
                LinearLayoutManager tagLayoutManager = new LinearLayoutManager(getBaseContext());
                tagLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                RecyclerView listViewTag = findViewById(R.id.tag_view);
                listViewTag.setLayoutManager(tagLayoutManager);
                listViewTag.setAdapter(tagAdapter);
                // Render title
                TextView title = findViewById(R.id.detail_title);
                title.setText(detail.getBookTitle());
                // Render author
                TextView author = findViewById(R.id.detail_author);
                author.setText(detail.getAuthor());
                // Render rating
                TextView rating = findViewById(R.id.detail_rating);
                RatingBar ratingBar = findViewById(R.id.detail_rating_bar);
                rating.setText(String.valueOf(detail.getRating()) + "/5.0");
                ratingBar.setRating(detail.getRating());

                // Render overlay
                View overlay = (View) findViewById(R.id.book_overlay);
                float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 84, getResources().getDisplayMetrics());
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) overlay.getLayoutParams();
                title.post(new Runnable() {
                    @Override
                    public void run() {
                        int au = title.getLineCount();
                        params.height = (int) (height + (int) (au * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics())));
                        overlay.setLayoutParams(params);
                    }
                });
                //Render desc
                TextView desc = findViewById(R.id.detail_desc);
                desc.setText(detail.getBookDescription());
                //Render cover
                Picasso.get()
                        .load(detail.getBookCover())
                        .transform(new BlurTransformation(getBaseContext(), 18, 2))
                        .into((ImageView) findViewById(R.id.blur_bg));

                Picasso.get()
                        .load(detail.getBookCover())
                        .into((ImageView) findViewById(R.id.cover));
            }

            @Override
            public void onFailure(Call<DetailReponseObj> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });

        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Window window = getWindow();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        View view = findViewById(android.R.id.content).getRootView();

        //open search
        ImageView likeBtn = (ImageView) findViewById(R.id.like_btn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView title = findViewById(R.id.detail_title);

                SharedPreferences sharedPrefs = getSharedPreferences("userDataPrefs", MODE_PRIVATE);
                Boolean isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    Intent intent = new Intent(v.getContext(), SearchActivity.class);
                    intent.putExtra("EXTRA_TAG", "viễn tưởng");
                    v.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                    Toast.makeText(v.getContext(), "Hãy đăng nhập để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final LinearLayout readButton = (LinearLayout) findViewById(R.id.read_btn);
        final LinearLayout listenButton = (LinearLayout) findViewById(R.id.listen_btn);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);

                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE_TYPE", "read");
                Bundle tagValue = getIntent().getExtras();
                String id = tagValue.getString("EXTRA_BOOK_ID") != null ? tagValue.getString("EXTRA_BOOK_ID") : "1";
                extras.putString("EXTRA_MESSAGE_ID", id);
                intent.putExtras(extras);

                startActivity(intent);

            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE_TYPE", "listen");
                Bundle tagValue = getIntent().getExtras();
                String id = tagValue.getString("EXTRA_BOOK_ID") != null ? tagValue.getString("EXTRA_BOOK_ID") : "1";
                extras.putString("EXTRA_MESSAGE_ID", id);
                intent.putExtras(extras);

                startActivity(intent);
            }
        });


        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        bottomBar.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT
                        , 240 + this.getNavigationBarHeight()));

//        getWindow().setStatusBarColor(Color.parseColor("#20ff1111"));
//        getWindow().setNavigationBarColor(Color.parseColor("#20111111"));


        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(0);

        ReviewItem[] reviewItems = {
                new ReviewItem("Ôi hay quá", "https://icdn.dantri.com.vn/thumb_w/640/2019/04/16/33-1555425777506.jpg", "Bin Gết", (float) 4.5),
                new ReviewItem("Quá xá đỉnh ghê.", "https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102", "Cao Thắng", (float) 2.5),
                new ReviewItem("Truyện hay nhưng cờ Việt Nam chỉ có một sao nên vote 1 sao vậy.", "https://media.tinmoi.vn/2016/11/21/Cac-ca-si-noi-tieng-Viet-Nam-thap-nien-90-1.jpg", "Đan Trường", (float) 1.0),
                new ReviewItem("Quá xá đỉnh", "https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102", "Cao Thắng", (float) 2.5),
                new ReviewItem("Quá xá đỉnh", "https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102", "Cao Thắng", (float) 2.5),
        };

        // Construct the data source
        final List<ReviewItem> listReview = new ArrayList<>(Arrays.asList(reviewItems));
        final RecyclerReviewAdapter adapter = new RecyclerReviewAdapter(listReview);

        RecyclerView listView = view.findViewById(R.id.list_review);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);


        final AppCompatActivity self = this;

        final Dialog reviewDialog = new Dialog(view.getContext());

        reviewDialog.setTitle(R.string.write_review);
        reviewDialog.setContentView(R.layout.review_dialog);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);

        reviewDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        //write review

        TextView more = (TextView) findViewById(R.id.write_review);
        more.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), agones.class);
                //startActivityForResult(myIntent, 0);
                reviewDialog.show();
            }
        });

        Button closeReviewDialogBtn = (Button) reviewDialog.findViewById(R.id.cancel_review_dialog);
        closeReviewDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
            }
        });

        EditText commentText = (EditText) reviewDialog.findViewById(R.id.comment);
        RatingBar ratingBar = (RatingBar) reviewDialog.findViewById(R.id.rate);

        Button reviewPostDialogBtn = (Button) reviewDialog.findViewById(R.id.review_post);
        reviewPostDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessToken = sharedPrefs.getString("accessToken", "");
                String comment = commentText.getText().toString();
                int rate = (int) ratingBar.getRating();
                Call<BookRateResponseObj> callPostRate = detailService.rateBook("Bearer " + accessToken, id, rate, comment);
                callPostRate.enqueue(new Callback<BookRateResponseObj>() {
                    @Override
                    public void onResponse(Call<BookRateResponseObj> call, Response<BookRateResponseObj> response) {
                        if (response.body() != null) {
                            //success
                        } else {
                        }
                        reviewDialog.dismiss();

                    }

                    @Override
                    public void onFailure(Call<BookRateResponseObj> call, Throwable t) {
                        Toast.makeText(getBaseContext(), R.string.err_action, Toast.LENGTH_SHORT).show();
                        reviewDialog.dismiss();
                    }
                });


            }
        });

    }

    public int getNavigationBarHeight() {
        Resources resources = this.getBaseContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}

