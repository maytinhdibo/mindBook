package com.mtc.mindbook.models.explore;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerNearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public ProgressBar progressBar = null;
    private List<NearItem> data = new ArrayList<>();

    public RecyclerNearAdapter(List<NearItem> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 1) {
            View view = inflater.inflate(R.layout.near_item, parent, false);
            return new RecyclerViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) == null ? 0 : 1;
    }

    public void setLoading(boolean value) {
        if (progressBar != null) {
            if (value) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
            holder.userName.setText(data.get(position).getName());
            holder.ratingBar.setRating(data.get(position).getBook().getRating());
            holder.bookAuthorName.setText(data.get(position).getBook().getAuthorName());
            holder.bookName.setText(data.get(position).getBook().getName());
            Picasso.get().load(data.get(position).getBook().getCover()).into(holder.avt);
            holder.nearItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.openDetailPage(view.getContext(), String.valueOf(data.get(position).getBook().getId()));
                }
            });
        } else {
            LoadingViewHolder holder = (LoadingViewHolder) viewHolder;
            progressBar = holder.progressBar;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            ;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
//        TextView comment;
        TextView bookName;
        TextView bookAuthorName;
        ImageView avt;
        RatingBar ratingBar;
        LinearLayout nearItem;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            avt = (ImageView) itemView.findViewById(R.id.avt_img);
            ratingBar = (RatingBar) itemView.findViewById(R.id.reviewRating);
//            comment = (TextView) itemView.findViewById(R.id.comment);
//            comment.setEllipsize(TextUtils.TruncateAt.END);
            bookName = (TextView) itemView.findViewById(R.id.book_name);
            bookAuthorName = (TextView) itemView.findViewById(R.id.book_author_name);
            nearItem = (LinearLayout) itemView.findViewById(R.id.near_item);
        }
    }
}