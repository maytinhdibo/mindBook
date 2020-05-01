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
import com.mtc.mindbook.models.BookItem;
import com.mtc.mindbook.models.explore.NearItem;
import com.mtc.mindbook.models.explore.RecyclerNearAdapter;
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
                new ShareItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItem(111, "https://static.wixstatic.com/media/9c4410_876c178659774d75aa6d9ec9fadfa4a2~mv2_d_1650_2550_s_2.jpg/v1/fill/w_270,h_412,al_c,q_80,usm_0.66_1.00_0.01/WILD%20LIGHT%20EBOOK.webp", "Quyển sách 1", "Tác Giả", (float) 3)),
                new ShareItem("Cũng như con ong biến trăm hoa thành một mật, Nguyễn Nhật Ánh lặng lẽ đi tìm mật ngọt từ tuổi thơ của mỗi người, để kết tinh thành những trang sách làm lay động trái tim của biết bao thế hệ.", "Bin Gết"
                        , new BookItem(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 4.5)),
                new ShareItem("Ai trong chúng ta chẳng có một người để nhớ, để thương, để vấn vương?", "Bin Gết"
                        , new BookItem(111, "https://pictures.abebooks.com/MICHELLANTEIGNE/4187602196.jpg", "Khi Loài Thú Xa Nhau", "Lê Uyên", (float) 2.5)),
                new ShareItem("Ai trong chúng ta chẳng có một người để nhớ, để thương, để vấn vương?", "Bin Gết"
                        , new BookItem(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 4.5)),
                new ShareItem("Cũng như con ong biến trăm hoa thành một mật, Nguyễn Nhật Ánh lặng lẽ đi tìm mật ngọt từ tuổi thơ của mỗi người, để kết tinh thành những trang sách làm lay động trái tim của biết bao thế hệ.", "Bin Gết"
                        , new BookItem(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 4.5)),
                new ShareItem("Ôi con ong biến trăm hoa thành một mật, Nguyễn Nhật Ánh lặng lẽ đi tìm mật ngọt từ tuổi thơ của mỗi người, để kết tinh thành những trang sách làm lay động trái tim của biết bao thế hệ.", "Bin Gết"
                        , new BookItem(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 4.5)),
                null
        };

        // Construct the data source
        final List<ShareItem> listReview = new ArrayList<>(Arrays.asList(shareItems));
        final RecyclerShareAdapter adapter = new RecyclerShareAdapter(listReview);
        RecyclerView listView = rootView.findViewById(R.id.share_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);


//        rootView.

        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == listReview.size() - 1) {

                        Log.d("Load", "onScrolled: ");
                        adapter.setLoading(false);

//                        isLoading = true;
                    }
                }
            }

        });


        return rootView;
    }
}
