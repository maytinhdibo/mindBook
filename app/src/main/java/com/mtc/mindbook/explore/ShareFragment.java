package com.mtc.mindbook.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.review.RecyclerReviewAdapter;
import com.mtc.mindbook.models.review.ReviewItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);

        ReviewItem[] reviewItems = {
                new ReviewItem("Ôi hay quá","https://icdn.dantri.com.vn/thumb_w/640/2019/04/16/33-1555425777506.jpg","Bin Gết", (float) 4.5),
                new ReviewItem("Quá xá đỉnh ghê.","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ReviewItem("Truyện hay nhưng cờ Việt Nam chỉ có một sao nên vote 1 sao vậy.","https://media.tinmoi.vn/2016/11/21/Cac-ca-si-noi-tieng-Viet-Nam-thap-nien-90-1.jpg","Đan Trường", (float) 1.0),
                new ReviewItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ReviewItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ReviewItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ReviewItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ReviewItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),

        };

        // Construct the data source
        final List<ReviewItem> listReview =  new ArrayList<>(Arrays.asList(reviewItems));
        final RecyclerReviewAdapter adapter = new RecyclerReviewAdapter(listReview);

        RecyclerView listView = rootView.findViewById(R.id.share_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);

        return rootView;
    }
}
