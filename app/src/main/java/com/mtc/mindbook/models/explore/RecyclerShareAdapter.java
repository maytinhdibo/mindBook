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
import com.mtc.mindbook.models.responseObj.ShareItem;
import com.mtc.mindbook.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ProgressBar progressBar = null;
    private List<ShareItem> data = new ArrayList<>();

    public RecyclerShareAdapter(List<ShareItem> data) {
        this.data = data;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 1) {
            View view = inflater.inflate(R.layout.share_item, parent, false);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
            holder.comment.setText(data.get(position).getRatingComment());
            holder.userName.setText(data.get(position).getFullName());
            holder.ratingBar.setRating(data.get(position).getRatingNum());
            holder.bookAuthorName.setText(data.get(position).getAuthorName());
            holder.bookName.setText(data.get(position).getBookTitle());
            Picasso.get().load(data.get(position).getBookCover()).into(holder.avt);
            holder.shareItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.openDetailPage(view.getContext(), String.valueOf(data.get(position).getBookId()));
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
        TextView comment;
        TextView bookName;
        TextView bookAuthorName;
        ImageView avt;
        RatingBar ratingBar;
        LinearLayout shareItem;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            avt = (ImageView) itemView.findViewById(R.id.avt_img);
            ratingBar = (RatingBar) itemView.findViewById(R.id.reviewRating);
            comment = (TextView) itemView.findViewById(R.id.comment);
            comment.setEllipsize(TextUtils.TruncateAt.END);
            bookName = (TextView) itemView.findViewById(R.id.book_name);
            bookAuthorName = (TextView) itemView.findViewById(R.id.book_author_name);
            shareItem = (LinearLayout) itemView.findViewById(R.id.share_item);
        }
    }
}

