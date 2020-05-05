package com.mtc.mindbook.explore;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.BookItemOld;
import com.mtc.mindbook.models.explore.NearItem;
import com.mtc.mindbook.models.explore.RecyclerNearAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearFragment extends Fragment implements LocationListener {
    private LocationManager locationManager;
    private LinearLayout locationAlert;
    private RecyclerView nearListView;
    private Location location;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_near, container, false);

        locationAlert = rootView.findViewById(R.id.location_req);

        NearItem[] shareItems = {
                new NearItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItemOld(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 4.5)),
                new NearItem("Cá nhân mình đánh giá cao về giao diện này bởi nó rất mượt và nhẹ.", "Xuân Đức"
                        , new BookItemOld(111, "https://static.wixstatic.com/media/9c4410_876c178659774d75aa6d9ec9fadfa4a2~mv2_d_1650_2550_s_2.jpg/v1/fill/w_270,h_412,al_c,q_80,usm_0.66_1.00_0.01/WILD%20LIGHT%20EBOOK.webp", "Quyển sách 1", "Tác Giả", (float) 3)),
                new NearItem("Biết nhóm từ một người thật đặc biệt và giờ gắn bó với những bài nhạc của nhóm còn người đó thì không", "Anh Tuấn"
                        , new BookItemOld(111, "https://pictures.abebooks.com/MICHELLANTEIGNE/4187602196.jpg", "Khi Loài Thú Xa Nhau", "Lê Uyên", (float) 2.5)),
                new NearItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItemOld(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 1.5)),
                new NearItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItemOld(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 1.5)),
                new NearItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItemOld(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 1.5)),
                new NearItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItemOld(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 1.5)),
                null
        };


        // Construct the data source
        final List<NearItem> listShare = new ArrayList<>(Arrays.asList(shareItems));
        final RecyclerNearAdapter adapter = new RecyclerNearAdapter(listShare);


        nearListView = rootView.findViewById(R.id.near_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        nearListView.setLayoutManager(layoutManager);

        nearListView.setAdapter(adapter);

        Button openLocation = rootView.findViewById(R.id.open_location_cog);

        openLocation.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent);
                                            }
                                        }
        );

        nearListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isLoading = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("Position1", String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    Log.d("Position", String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == listShare.size() - 1) {

                        Log.d("Load", "onScrolled: ");
//                        adapter.setLoading(false);
//                        isLoading = true;
                    }
                }
            }

        });


        checkLocation(rootView, true);


        return rootView;
    }

    public void checkLocation(View rootView, boolean request) {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            locationAlert.setVisibility(View.VISIBLE);
            nearListView.setVisibility(View.GONE);

            if (request) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        123);
            }

        } else {
            locationAlert.setVisibility(View.GONE);
            nearListView.setVisibility(View.VISIBLE);
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            Log.d("Location", location.getLatitude() + ":" + location.getLongitude());

        }
    }

    private boolean firstTime = false;

    @Override
    public void onResume() {
        super.onResume();
        if (firstTime) {
            firstTime = false;
        } else {
            checkLocation(getView(), false);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latT = location.getLatitude();
        double longT = location.getLongitude();
        Log.d("Location", "onLocationChanged: " + latT + ";" + longT);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
