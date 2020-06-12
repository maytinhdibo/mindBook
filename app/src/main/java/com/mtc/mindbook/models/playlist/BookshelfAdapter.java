package com.mtc.mindbook.models.playlist;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.PlaylistActivity;
import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDataResponseObj;

import java.util.ArrayList;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.RecyclerViewHolder> {
    private List<PlaylistDataResponseObj> data = new ArrayList<>();

    public BookshelfAdapter(List<PlaylistDataResponseObj> data) {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.playlist_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookshelfAdapter.RecyclerViewHolder holder, final int position) {
        holder.playlistName.setText(data.get(position).getName());
        holder.bookCount.setText(data.get(position).getBooksCount() + " Cuốn sách");
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlaylistActivity.class);
                intent.putExtra("title", data.get(position).getName());
                v.getContext().startActivity(intent);
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
            playlistName = (TextView) itemView.findViewById(R.id.playlist_name);
            bookCount = (TextView) itemView.findViewById(R.id.playlist_book_count);
            container = itemView.findViewById(R.id.playlist_item_container);
        }
    }
}
