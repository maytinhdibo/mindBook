package com.mtc.mindbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.models.RecyclerViewAdapter;
import com.mtc.mindbook.models.foryou.RecyclerForYouViewAdapter;
import com.mtc.mindbook.models.responseObj.BookItem;
import com.mtc.mindbook.models.responseObj.banner.Banner;
import com.mtc.mindbook.models.responseObj.banner.BannerResponseObj;
import com.mtc.mindbook.models.responseObj.catetory.trending.BookTrendResponseObj;
import com.mtc.mindbook.models.responseObj.search.SearchResponseObj;
import com.mtc.mindbook.models.slideshow.SlideShowAdapter;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    RecyclerView listViewTrending;
    RecyclerView listViewForYou;
    RecyclerView randomListView;
    Context baseContext;
    APIService api = APIUtils.getUserService();

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseContext = getContext();
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rootView.findViewById(R.id.main_layout).setPaddingRelative(0, getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android")
        ), 0, 0);

        // Construct the data source
        final List<BookItem> listItem = new ArrayList<>();

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(listItem);

        // layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        // for you
        listViewForYou = rootView.findViewById(R.id.listview_foryou);
        listViewForYou.setLayoutManager(layoutManager);

        //category
        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        layoutManagerCategory.setOrientation(LinearLayoutManager.HORIZONTAL);

        randomListView = rootView.findViewById(R.id.listview_category);
        randomListView.setLayoutManager(layoutManagerCategory);

        // trending
        LinearLayoutManager layoutManagerTrending = new LinearLayoutManager(getContext());
        layoutManagerTrending.setOrientation(LinearLayoutManager.HORIZONTAL);


        listViewTrending = rootView.findViewById(R.id.listview_trending);
        listViewTrending.setLayoutManager(layoutManagerTrending);

        loadForYou(listViewForYou, getContext());

        loadRandom(randomListView, getContext());

        loadTrending(getContext());


        // Construct the data source

        SliderView sliderView = rootView.findViewById(R.id.imageSlider);

        SlideShowAdapter adapterSlide = new SlideShowAdapter(getContext());

        sliderView.setSliderAdapter(adapterSlide);
        sliderView.setScrollTimeInSec(5);
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

    private void loadTrending(Context context) {
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
                try {
                    //Toast.makeText(context, R.string.err_network, Toast.LENGTH_SHORT).show();
                    if (baseContext != null) {
                        Intent networkErr = new Intent(baseContext, NetworkErrorActivity.class);
                        startActivity(networkErr);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void loadForYou(RecyclerView view, Context context) {
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);

        boolean isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            loadRandom(view, context);
            return;
        }

        String accessToken = sharedPrefs.getString("accessToken", "");

        Call<SearchResponseObj> callDetail = api.forYou("Bearer " + accessToken);
        callDetail.enqueue(new Callback<SearchResponseObj>() {
            @Override
            public void onResponse(Call<SearchResponseObj> call, Response<SearchResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                RecyclerForYouViewAdapter forYouAdapter = new RecyclerForYouViewAdapter(response.body().getData());
                view.setAdapter(forYouAdapter);
            }

            @Override
            public void onFailure(Call<SearchResponseObj> call, Throwable t) {
                try {
                    //Toast.makeText(context, R.string.err_network, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }
            }
        });
    }

    private void loadRandom(RecyclerView view, Context context) {
        Call<SearchResponseObj> callDetail = api.random();
        callDetail.enqueue(new Callback<SearchResponseObj>() {
            @Override
            public void onResponse(Call<SearchResponseObj> call, Response<SearchResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                RecyclerForYouViewAdapter forYouAdapter = new RecyclerForYouViewAdapter(response.body().getData());
                view.setAdapter(forYouAdapter);
            }

            @Override
            public void onFailure(Call<SearchResponseObj> call, Throwable t) {
                try {
                    //Toast.makeText(context, R.string.err_network, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }
            }
        });
    }

}
