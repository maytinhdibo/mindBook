package com.mtc.mindbook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.review.RecyclerReviewAdapter;
import com.mtc.mindbook.models.review.ReviewItem;
import com.mtc.mindbook.models.search.SearchViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;
import static com.mtc.mindbook.MainActivity.EXTRA_TAG;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_TYPE = "none";
    public static final String EXTRA_MESSAGE_ID = "none";

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
                Intent intent = new Intent(v.getContext(), SearchActivity.class);
                intent.putExtra("EXTRA_TAG", "viễn tưởng");
                v.getContext().startActivity(intent);
            }
        });




        final LinearLayout readButton = (LinearLayout) findViewById(R.id.read_btn);
        final  LinearLayout listenButton = (LinearLayout) findViewById(R.id.listen_btn);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);

                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE_TYPE", "read");
                extras.putString("EXTRA_MESSAGE_ID", "22");
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
                extras.putString("EXTRA_MESSAGE_ID", "3");
                intent.putExtras(extras);

                startActivity(intent);
            }
        });


        Picasso.get()
                .load("https://dspncdn.com/a1/media/692x/38/3a/21/383a215d646b32b00b3b44b58bd81fa1.jpg")
                .transform(new BlurTransformation(getBaseContext(), 18, 2))
                .into((ImageView) findViewById(R.id.blur_bg));

        Picasso.get()
                .load("https://dspncdn.com/a1/media/692x/38/3a/21/383a215d646b32b00b3b44b58bd81fa1.jpg")
                .into((ImageView) findViewById(R.id.cover));

        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        bottomBar.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT
                        , 240 + this.getNavigationBarHeight()));

//        getWindow().setStatusBarColor(Color.parseColor("#20ff1111"));
//        getWindow().setNavigationBarColor(Color.parseColor("#20111111"));


        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(0);


        ReviewItem[] reviewItems = {
                new ReviewItem("Ôi hay quá","https://icdn.dantri.com.vn/thumb_w/640/2019/04/16/33-1555425777506.jpg","Bin Gết", (float) 4.5),
                new ReviewItem("Quá xá đỉnh ghê.","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ReviewItem("Truyện hay nhưng cờ Việt Nam chỉ có một sao nên vote 1 sao vậy.","https://media.tinmoi.vn/2016/11/21/Cac-ca-si-noi-tieng-Viet-Nam-thap-nien-90-1.jpg","Đan Trường", (float) 1.0),
                new ReviewItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ReviewItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
        };

        // Construct the data source
        final List<ReviewItem> listReview =  new ArrayList<>(Arrays.asList(reviewItems));
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

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);

        reviewDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        //write review

        TextView more = (TextView) findViewById(R.id.write_review);
        more.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), agones.class);
                //startActivityForResult(myIntent, 0);
                reviewDialog.show();  //<-- See This!
            }
        });

        Button closeReviewDialogBtn = (Button) reviewDialog.findViewById(R.id.cancel_review_dialog);
        closeReviewDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
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

