package com.mtc.mindbook.explore;

import android.os.Bundle;
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

public class NearFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_near, container, false);

        NearItem[] shareItems = {
                new NearItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItem(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 4.5)),
                new NearItem("Cá nhân mình đánh giá cao về giao diện này bởi nó rất mượt và nhẹ.", "Xuân Đức"
                        , new BookItem(111, "https://static.wixstatic.com/media/9c4410_876c178659774d75aa6d9ec9fadfa4a2~mv2_d_1650_2550_s_2.jpg/v1/fill/w_270,h_412,al_c,q_80,usm_0.66_1.00_0.01/WILD%20LIGHT%20EBOOK.webp", "Quyển sách 1", "Tác Giả", (float) 3)),
                new NearItem("Biết nhóm từ một người thật đặc biệt và giờ gắn bó với những bài nhạc của nhóm còn người đó thì không", "Anh Tuấn"
                        , new BookItem(111, "https://pictures.abebooks.com/MICHELLANTEIGNE/4187602196.jpg", "Khi Loài Thú Xa Nhau", "Lê Uyên", (float) 2.5)),
                new NearItem("Tôi sống một thời thơ ấu chịu ảnh hưởng hoàn toàn của các anh chị tôi, hai chị và hai anh lớn, họ ca hát bài gì thì tôi lặp lại đúng bài đó, họ ngâm nga bài thơ nào thì tôi nhớ lõm bõm mấy câu của bài đó. Bây giờ kiểm điểm lại, về các bài hát tôi nhớ nhiều, gần như trọn vẹn ca điệu và ca từ của mỗi bài, còn thơ chỉ thuộc đây đó một số câu.", "Bin Gết"
                        , new BookItem(111, "https://res.cloudinary.com/fen-learning/image/upload/c_limit,w_320,h_475/infopls_images/images/HPusa5_320x475.jpg", "Quyển sách 1", "Tác Giả", (float) 1.5)),
        };

        // Construct the data source
        final List<NearItem> listShare =  new ArrayList<>(Arrays.asList(shareItems));
        final RecyclerNearAdapter adapter = new RecyclerNearAdapter(listShare);


        RecyclerView listView = rootView.findViewById(R.id.near_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);

        return rootView;
    }
}
