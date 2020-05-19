package com.mtc.mindbook.models.review;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.rating.RatingComment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerReviewAdapter extends RecyclerView.Adapter<RecyclerReviewAdapter.RecyclerViewHolder>{

    private List<RatingComment> data = new ArrayList<>();

    public RecyclerReviewAdapter(List<RatingComment> data) {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.comment.setText(data.get(position).getRatingComment());
        holder.userName.setText(data.get(position).getFullName());
        holder.ratingBar.setRating(data.get(position).getRatingNum());
        Picasso.get().load(data.get(position).getAvatar()).into(holder.avt);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView comment;
        ImageView avt;
        RatingBar ratingBar;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            avt = (ImageView) itemView.findViewById(R.id.avt_img);
            ratingBar = (RatingBar) itemView.findViewById(R.id.reviewRating);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }
}