package com.mtc.mindbook.models.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.SearchItem;
import com.mtc.mindbook.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.RecyclerViewHolder>{

    private List<SearchItem> data = new ArrayList<>();

    public SearchViewAdapter(List<SearchItem> data) {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.bookName.setText(data.get(position).getBookTitle());
        holder.authorName.setText(data.get(position).getAuthor());
        holder.rating.setText(String.valueOf(data.get(position).getRating()));
        Picasso.get().load(data.get(position).getBookCover()).into(holder.bookImage);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openDetailPage(view.getContext(), String.valueOf(data.get(position).getBookId()));
            }
        }); ;
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
        LinearLayout item;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            bookImage = (ImageView) itemView.findViewById(R.id.songImageView);
            bookName = (TextView) itemView.findViewById(R.id.title);
            authorName = (TextView) itemView.findViewById(R.id.author);
            rating = (TextView) itemView.findViewById(R.id.rating);
            item = (LinearLayout) itemView.findViewById(R.id.search_item);
        }
    }
}