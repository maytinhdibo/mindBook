package com.mtc.mindbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.model.EntryItem;
import com.mtc.mindbook.model.RecyclerViewAdapter;
import com.mtc.mindbook.model.slideshow.SlideShowAdapter;
import com.smarteist.autoimageslider.SliderView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        EntryItem[] todoItems = {
                new EntryItem(1, "https://a.wattpad.com/cover/155025710-288-k448920.jpg", "Tuyển tập Ngô Tất Tố"),
                new EntryItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg","Mắt biếc"),
                new EntryItem(3, "https://cdn0.fahasa.com/media/flashmagazine/images/page_images/nha_gia_kim_tai_ban_2017/2019_11_04_14_41_25_1.jpg","Nhà giả kim"),
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

        //


        SliderView sliderView = rootView.findViewById(R.id.imageSlider);

        SlideShowAdapter adapterSlide = new SlideShowAdapter(getContext());

        adapterSlide.renewItems(listItem);

        sliderView.setSliderAdapter(adapterSlide);

//        ImageView imageView = rootView.findViewById(R.id.imageView);
//        Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(imageView);

//        WebView myWebView = (WebView) rootView.findViewById(R.id.webview);
//        myWebView.setWebViewClient(new WebViewClient());
//        myWebView.loadUrl("https://google.com");

        return rootView;
    }

}
