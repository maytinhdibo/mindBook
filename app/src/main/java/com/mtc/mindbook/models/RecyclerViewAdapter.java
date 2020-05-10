package com.mtc.mindbook.models;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.BookItem;
import com.mtc.mindbook.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{

    private List<BookItem> data = new ArrayList<>();

    public RecyclerViewAdapter(List<BookItem> data) {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.book_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.songName.setText(data.get(position).getBookTitle());
        Picasso.get().load(data.get(position).getBookCover()).into(holder.songImage);
        holder.itemCon.setOnClickListener(new View.OnClickListener() {
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
        TextView songName;
        ImageView songImage;
        CardView itemCon;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            songImage = (ImageView) itemView.findViewById(R.id.songImageView);
            songName = (TextView) itemView.findViewById(R.id.title);
            songName.setEllipsize(TextUtils.TruncateAt.END);
            itemCon = (CardView) itemView.findViewById(R.id.songCard);
        }
    }
}