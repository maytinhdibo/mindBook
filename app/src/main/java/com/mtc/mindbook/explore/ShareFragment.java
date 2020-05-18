package com.mtc.mindbook.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.explore.RecyclerShareAdapter;
import com.mtc.mindbook.models.responseObj.explore.share.ShareItem;
import com.mtc.mindbook.models.responseObj.explore.share.ShareItemResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareFragment extends Fragment {
    private int page = 1;
    RecyclerView listView;
    List<ShareItem> listReview = new ArrayList<>();
    boolean loadEnd = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);

        listView = rootView.findViewById(R.id.share_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);


        listReview.add(null);

        RecyclerShareAdapter adapter = new RecyclerShareAdapter(listReview);
        listView.setAdapter(adapter);


        loadNewPage();

        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("Position1", String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!loadEnd) {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == listReview.size() - 1) {
                        page++;
                        loadNewPage();
                        return;
                    }
                }
            }

        });

        return rootView;
    }

    private void loadNewPage() {
        APIService shareService = null;
        shareService = APIUtils.getUserService();
        Call<ShareItemResponseObj> callDetail = shareService.getShares(page);
        callDetail.enqueue(new Callback<ShareItemResponseObj>() {
            @Override
            public void onResponse(Call<ShareItemResponseObj> call, Response<ShareItemResponseObj> response) {
                if (response.body() == null) {
                    onFailure(call, null);
                    return;
                }
                if (response.body().getData().size() == 0) {
                    RecyclerShareAdapter adapter = (RecyclerShareAdapter) listView.getAdapter();
                    adapter.setLoading(false);
                    loadEnd = true;
                }
                listReview.remove(null);
                listReview.addAll(response.body().getData());
                listReview.add(null);
                listView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ShareItemResponseObj> call, Throwable t) {
                Toast.makeText(getContext(), R.string.err_network, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
