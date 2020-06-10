package com.mtc.mindbook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
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

import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.detail.DetailReponseObj;
import com.mtc.mindbook.models.responseObj.detail.BookDetail;
import com.mtc.mindbook.models.responseObj.rating.RatingComment;
import com.mtc.mindbook.models.responseObj.rating.RatingCommentsResponseObj;
import com.mtc.mindbook.models.review.RecyclerReviewAdapter;
import com.mtc.mindbook.models.tag.TagAdapter;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    List<RatingComment> ratingCommentList = new ArrayList<>();

    private APIService apiServices = APIUtils.getUserService();

    int limitComment = 2;
    int pageComment = 1;

    Button loadMoreBtn = null;

    SharedPreferences sharedPrefs = null;
    boolean isLoggedIn = false;
    String id;

    @Override
    protected void onStart() {
        sharedPrefs = getSharedPreferences("userDataPrefs", MODE_PRIVATE);
        isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false);

        sharedPrefs = getBaseContext().getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);

        Call<DetailReponseObj> callDetail = apiServices.detailBook(id);
        callDetail.enqueue(new Callback<DetailReponseObj>() {
            @Override
            public void onResponse(Call<DetailReponseObj> call, Response<DetailReponseObj> response) {
                if(response.body()==null) return;
                BookDetail bookDetail = response.body().getData().get(0);
                // Render data
                // Render tag
                final List<String> listItem = bookDetail.getCategories();
                final TagAdapter tagAdapter = new TagAdapter(listItem);
                LinearLayoutManager tagLayoutManager = new LinearLayoutManager(getBaseContext());
                tagLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                RecyclerView listViewTag = findViewById(R.id.tag_view);
                listViewTag.setLayoutManager(tagLayoutManager);
                listViewTag.setAdapter(tagAdapter);
                // Render title
                TextView title = findViewById(R.id.detail_title);
                title.setText(bookDetail.getBookTitle());
                // Render author
                TextView author = findViewById(R.id.detail_author);
                author.setText(bookDetail.getAuthor());
                // Render rating
                TextView rating = findViewById(R.id.detail_rating);
                RatingBar ratingBar = findViewById(R.id.detail_rating_bar);
                rating.setText(String.format("%.01f", bookDetail.getRating()) + "/5.0");
                ratingBar.setRating(bookDetail.getRating());

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
                desc.setText(bookDetail.getBookDescription());
                //Render cover
                Picasso.get()
                        .load(bookDetail.getBookCover())
                        .transform(new BlurTransformation(getBaseContext(), 18, 2))
                        .into((ImageView) findViewById(R.id.blur_bg));

                Picasso.get()
                        .load(bookDetail.getBookCover())
                        .into((ImageView) findViewById(R.id.cover));
            }

            @Override
            public void onFailure(Call<DetailReponseObj> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });


        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadComment(limitComment, ++pageComment);
            }
        });

        super.onStart();

    }

    private void loadComment(int limit, int page) {
        final RecyclerReviewAdapter adapter = new RecyclerReviewAdapter(ratingCommentList);
        loadMoreBtn.setText(getString(R.string.loading));
        RecyclerView listView = findViewById(R.id.list_review);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);

        Call<RatingCommentsResponseObj> callRatingComment = apiServices.getRatingComment(id, limit, page);
        callRatingComment.enqueue(new Callback<RatingCommentsResponseObj>() {
            @Override
            public void onResponse(Call<RatingCommentsResponseObj> call, Response<RatingCommentsResponseObj> response) {
                if (response.body() == null) {
                    Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (page == 1) ratingCommentList.clear();
                ratingCommentList.addAll(response.body().getData());
                adapter.notifyDataSetChanged();
                if (response.body().getData().size() < limit) {
                    loadMoreBtn.setVisibility(View.GONE);
                } else {
                    loadMoreBtn.setText(getString(R.string.loadMoreReview));
                }
            }

            @Override
            public void onFailure(Call<RatingCommentsResponseObj> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Bundle tagValue = getIntent().getExtras();
        id = tagValue.getString("EXTRA_BOOK_ID") != null ? tagValue.getString("EXTRA_BOOK_ID") : "1";


        View view = findViewById(android.R.id.content).getRootView();

        //open search
        ImageView likeBtn = (ImageView) findViewById(R.id.like_btn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn) {
                    Intent intent = new Intent(v.getContext(), SearchActivity.class);
                    intent.putExtra("EXTRA_TAG", "viễn tưởng");
                    v.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                    Toast.makeText(v.getContext(), R.string.logInRequest, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE_TYPE", "listen");
                Bundle tagValue = getIntent().getExtras();
                String id = tagValue.getString("EXTRA_BOOK_ID") != null ? tagValue.getString("EXTRA_BOOK_ID") : "1";
                extras.putString("EXTRA_MESSAGE_ID", id);
                intent.putExtras(extras);

                startActivity(intent);
            }
        });

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
                if (!isLoggedIn) {
                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                    Toast.makeText(view.getContext(), R.string.logInRequest, Toast.LENGTH_SHORT).show();
                    return;
                }
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
                if (rate == 0) {
                    Toast.makeText(getBaseContext(), R.string.require_star, Toast.LENGTH_SHORT).show();
                    return;
                }
                Call<DefaultResponseObj> callPostRate = apiServices.rateBook("Bearer " + accessToken, id, rate, comment);
                callPostRate.enqueue(new Callback<DefaultResponseObj>() {
                    @Override
                    public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                        if (response.body() != null) {
                            loadComment(limitComment, 1);
                        } else {

                        }
                        reviewDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                        Toast.makeText(getBaseContext(), R.string.err_action, Toast.LENGTH_SHORT).show();
                        reviewDialog.dismiss();
                    }
                });


            }
        });

        loadMoreBtn = (Button) findViewById(R.id.load_comment);


        loadComment(limitComment, pageComment);

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

