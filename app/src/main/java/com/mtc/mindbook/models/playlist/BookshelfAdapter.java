package com.mtc.mindbook.models.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDataResponseObj;
import com.mtc.mindbook.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.RecyclerViewHolder> {
    private List<PlaylistDataResponseObj> data = new ArrayList<>();
    Context context = null;

    public BookshelfAdapter(List<PlaylistDataResponseObj> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.playlist_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookshelfAdapter.RecyclerViewHolder holder, final int position) {
        String playlistName = position == 0 ? context.getResources().getString(R.string.favorite_list_name) : data.get(position).getName();

        holder.playlistName.setText(playlistName);
        holder.bookCount.setText(data.get(position).getBooksCount() + " ");
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).getBooksCount() == 0) {
                    Toast.makeText(v.getContext(), context.getResources().getString(R.string.no_book_in_playlist), Toast.LENGTH_SHORT).show();
                } else {
                    Utils.openPlaylistDetail(v.getContext(), data.get(position).getPlaylistId(), data.get(position).getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        TextView bookCount;
        LinearLayout container;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist_name);
            bookCount = itemView.findViewById(R.id.playlist_book_count);
            container = itemView.findViewById(R.id.playlist_item_container);
        }
    }
}
