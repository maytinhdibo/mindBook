package com.mtc.soundme;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.soundme.model.EntryItem;
import com.mtc.soundme.model.RecyclerViewAdapter;
import com.mtc.soundme.model.SongsAdapter;
import com.squareup.picasso.Picasso;


import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        EntryItem[] todoItems = {
                new EntryItem(1, "https://i1.sndcdn.com/artworks-000553291293-dy0378-t500x500.jpg", "Hard, granular cheese"),
                new EntryItem(2, "https://nhacchuonghay.me/wp-content/uploads/2019/12/nhac-chuong-duyen-am-hoang-thuy-linh-mp3.jpg","Italian whey cheese"),
                new EntryItem(3, "https://photo-zmp3.zadn.vn/cover/1/6/7/6/16764a3fec6a43514405817854577bd3.jpg","Italian cow's milk cheese"),
                new EntryItem(4, "https://photo-zmp3.zadn.vn/cover/1/6/7/6/16764a3fec6a43514405817854577bd3.jpg","Southern Italian buffalo milk cheese"),
                new EntryItem(5, "https://photo-zmp3.zadn.vn/cover/1/6/7/6/16764a3fec6a43514405817854577bd3.jpg","Firm, cow's milk cheese"),
        };

        // Construct the data source
        final List<EntryItem> listItem =  new ArrayList<>(Arrays.asList(todoItems));

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(listItem);

        RecyclerView listView = rootView.findViewById(R.id.listview_tasks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);

//        ImageView imageView = rootView.findViewById(R.id.imageView);
//        Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(imageView);

//        WebView myWebView = (WebView) rootView.findViewById(R.id.webview);
//        myWebView.setWebViewClient(new WebViewClient());
//        myWebView.loadUrl("https://google.com");

        return rootView;
    }

}
