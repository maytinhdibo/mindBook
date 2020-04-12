package com.mtc.mindbook.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{

    private List<EntryItem> data = new ArrayList<>();

    public RecyclerViewAdapter(List<EntryItem> data) {
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
        holder.songName.setText(data.get(position).getName());
        Picasso.get().load(data.get(position).getCover()).into(holder.songImage);
        holder.itemCon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, data.get(position).getName());
                view.getContext().startActivity(intent);
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
            itemCon = (CardView) itemView.findViewById(R.id.songCard);
        }
    }
}