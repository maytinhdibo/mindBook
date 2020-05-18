package com.mtc.mindbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.BookItemOld;
import com.mtc.mindbook.models.RecyclerViewAdapter;
import com.mtc.mindbook.models.responseObj.BookItem;
import com.mtc.mindbook.models.responseObj.BookTrendResponseObj;
import com.mtc.mindbook.models.responseObj.banner.Banner;
import com.mtc.mindbook.models.responseObj.banner.BannerResponseObj;
import com.mtc.mindbook.models.slideshow.SlideShowAdapter;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    RecyclerView listViewTrending;
    APIService api = APIUtils.getUserService();

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_home, container, false);

//        BookItem[] todoItems = {
//                new BookItem(1, "https://a.wattpad.com/cover/155025710-288-k448920.jpg", "Tuyển tập Ngô Tất Tố"),
//                new BookItem(2, "https://genbooks.net/wp-content/uploads/2019/07/mat-biec.jpg","Mắt biếc"),
//                new BookItem(3, "https://cdn0.fahasa.com/media/flashmagazine/images/page_images/nha_gia_kim_tai_ban_2017/2019_11_04_14_41_25_1.jpg","Nhà giả kim"),
//                new BookItem(4, "https://photo-zmp3.zadn.vn/cover/1/6/7/6/16764a3fec6a43514405817854577bd3.jpg","Southern Italian buffalo milk cheese"),
//                new BookItem(5, "https://photo-zmp3.zadn.vn/cover/1/6/7/6/16764a3fec6a43514405817854577bd3.jpg","Firm, cow's milk cheese"),
//        };

        // Construct the data source
        final List<BookItem> listItem = new ArrayList<>();

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(listItem);

        // layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        // for you
        RecyclerView listViewForYou = rootView.findViewById(R.id.listview_foryou);
        listViewForYou.setLayoutManager(layoutManager);
        listViewForYou.setAdapter(adapter);

        // trending
        LinearLayoutManager layoutManagerTreding = new LinearLayoutManager(getContext());
        layoutManagerTreding.setOrientation(LinearLayoutManager.HORIZONTAL);


        listViewTrending = rootView.findViewById(R.id.listview_trending);
        listViewTrending.setLayoutManager(layoutManagerTreding);

        loadTrending();


        // Construct the data source

        SliderView sliderView = rootView.findViewById(R.id.imageSlider);

        SlideShowAdapter adapterSlide = new SlideShowAdapter(getContext());


        sliderView.setSliderAdapter(adapterSlide);
        sliderView.startAutoCycle();

        Call<BannerResponseObj> callBanner = api.getBanners();
        callBanner.enqueue(new Callback<BannerResponseObj>() {
            @Override
            public void onResponse(Call<BannerResponseObj> call, Response<BannerResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                List<Banner> listSlide = new ArrayList<>();
                listSlide = response.body().getData();
                adapterSlide.renewItems(listSlide);
            }
            @Override
            public void onFailure(Call<BannerResponseObj> call, Throwable t) {

            }
        });


        ImageView search = rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent searchIntent = new Intent(view.getContext(), SearchActivity.class);
                startActivityForResult(searchIntent, 0);
            }
        });

        return rootView;
    }

    private void loadTrending() {
        Call<BookTrendResponseObj> callDetail = api.trending();
        callDetail.enqueue(new Callback<BookTrendResponseObj>() {
            @Override
            public void onResponse(Call<BookTrendResponseObj> call, Response<BookTrendResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                RecyclerViewAdapter trendAdapter = new RecyclerViewAdapter(response.body().getData());
                listViewTrending.setAdapter(trendAdapter);
            }

            @Override
            public void onFailure(Call<BookTrendResponseObj> call, Throwable t) {
                Toast.makeText(getContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
