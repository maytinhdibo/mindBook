package com.mtc.mindbook.models.explore;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerNearAdapter extends RecyclerView.Adapter<RecyclerNearAdapter.RecyclerViewHolder>{

    private List<NearItem> data = new ArrayList<>();

    public RecyclerNearAdapter(List<NearItem> data) {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.near_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
//        holder.songName.setText(data.get(position).getName());
//        Picasso.get().load(data.get(position).getCover()).into(holder.songImage);
//        holder.itemCon.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(view.getContext(), DetailActivity.class);
//                intent.putExtra(EXTRA_MESSAGE, data.get(position).getName());
//                view.getContext().startActivity(intent);
//            }
//        }); ;
//        holder.comment.setText(data.get(position).getComment());
        holder.userName.setText(data.get(position).getName());
        holder.ratingBar.setRating(data.get(position).getBook().getRating());
        holder.bookAuthorName.setText(data.get(position).getBook().getAuthorName());
        holder.bookName.setText(data.get(position).getBook().getName());
        Picasso.get().load(data.get(position).getBook().getCover()).into(holder.avt);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
//        TextView comment;
        TextView bookName;
        TextView bookAuthorName;
        ImageView avt;
        RatingBar ratingBar;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            avt = (ImageView) itemView.findViewById(R.id.avt_img);
            ratingBar = (RatingBar) itemView.findViewById(R.id.reviewRating);
//            comment = (TextView) itemView.findViewById(R.id.comment);
//            comment.setEllipsize(TextUtils.TruncateAt.END);
            bookName = (TextView) itemView.findViewById(R.id.book_name);
            bookAuthorName = (TextView) itemView.findViewById(R.id.book_author_name);
        }
    }
}