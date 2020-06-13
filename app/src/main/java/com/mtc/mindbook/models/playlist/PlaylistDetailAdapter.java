package com.mtc.mindbook.models.playlist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDetailItem;
import com.mtc.mindbook.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.RecyclerViewHolder> {

    private List<PlaylistDetailItem> data = new ArrayList<>();

    public PlaylistDetailAdapter(List<PlaylistDetailItem> data) {
        this.data = data;
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
                Log.d("log", "onClick: " + data.get(position).getBookId());
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