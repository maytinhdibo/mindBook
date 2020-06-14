package com.mtc.mindbook.models.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDetailItem;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;
import com.mtc.mindbook.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.RecyclerViewHolder> {

    private List<PlaylistDetailItem> data = new ArrayList<>();
    Integer playlistId;
    private APIService apiService = APIUtils.getUserService();
    private Context context;

    public PlaylistDetailAdapter(List<PlaylistDetailItem> data, Integer playlistId) {
        this.data = data;
        this.playlistId = playlistId;
        this.context = context;
    }

    @Override
    public PlaylistDetailAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.playlist_detail_item, parent, false);
        return new PlaylistDetailAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistDetailAdapter.RecyclerViewHolder holder, final int position) {
        holder.bookName.setText(data.get(position).getBookTitle());
        holder.authorName.setText(data.get(position).getAuthor());
        holder.rating.setText(String.valueOf(data.get(position).getRating()));
        Picasso.get().load(data.get(position).getBookCover()).into(holder.bookImage);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openDetailPage(v.getContext(), data.get(position).getBookId());
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = v.getContext().getSharedPreferences("userDataPrefs", MODE_PRIVATE);
                String token = "Bearer " + sharedPrefs.getString("accessToken", "");
                Call<DefaultResponseObj> delete = apiService.deleteBookFromPlaylist(token, playlistId, data.get(position).getBookId());
                delete.enqueue(new Callback<DefaultResponseObj>() {
                    @Override
                    public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                        if (response.body().getMesssage().equals("Success")) {
                            Toast.makeText(v.getContext(), "Đã xóa " + data.get(position).getBookTitle() + " khỏi danh sách", Toast.LENGTH_SHORT).show();
                            data.remove(position);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(v.getContext(), "Thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                        Toast.makeText(v.getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView bookName;
        ImageView bookImage;
        TextView authorName;
        TextView rating;
        TextView delete;
        LinearLayout container;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.playlist_detail_item_songImageView);
            bookName = itemView.findViewById(R.id.playlist_detail_item_title);
            authorName = itemView.findViewById(R.id.playlist_detail_item_author);
            rating = itemView.findViewById(R.id.playlist_detail_item_rating);
            delete = itemView.findViewById(R.id.playlist_detail_item_delete);
            container = itemView.findViewById(R.id.playlist_detail_item_container);
        }
    }
}