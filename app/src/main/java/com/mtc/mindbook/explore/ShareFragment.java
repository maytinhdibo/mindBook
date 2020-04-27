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
import com.mtc.mindbook.models.explore.RecyclerShareAdapter;
import com.mtc.mindbook.models.explore.ShareItem;
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

        ShareItem[] shareItems = {
                new ShareItem("Ôi hay quá","https://images3.penguinrandomhouse.com/cover/9780525619772","Bin Gết", (float) 4.5),
                new ShareItem("Quá xá đỉnh ghê.","https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg","Cao Thắng", (float) 2.5),
                new ShareItem("Truyện hay nhưng cờ Việt Nam chỉ có một sao nên vote 1 sao vậy.","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1364320844l/17695302.jpg","Đan Trường", (float) 1.0),
                new ShareItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ShareItem("Quá xá đỉnh","https://images-na.ssl-images-amazon.com/images/I/51KlWgyfRoL.jpg","Cao Thắng", (float) 2.5),
                new ShareItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ShareItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),
                new ShareItem("Quá xá đỉnh","https://st.galaxypub.vn/staticFile/Subject/2014/10/07/2942145/ongcaothang5-30215_7211551.jpg?w=102","Cao Thắng", (float) 2.5),

        };

        // Construct the data source
        final List<ShareItem> listShare =  new ArrayList<>(Arrays.asList(shareItems));
        final RecyclerShareAdapter adapter = new RecyclerShareAdapter(listShare);

        RecyclerView listView = rootView.findViewById(R.id.share_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);

        return rootView;
    }
}
