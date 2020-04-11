package com.mtc.mindbook.model.slideshow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.mtc.mindbook.DetailActivity;
import com.mtc.mindbook.R;
import com.mtc.mindbook.model.EntryItem;
import com.mtc.mindbook.utils.Utils;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;

public class SlideShowAdapter extends
        SliderViewAdapter<SlideShowAdapter.SliderAdapterVH> {

    private Context context;
    private List<EntryItem> mSliderItems = new ArrayList<>();

    public SlideShowAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<EntryItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(EntryItem EntryItem) {
        this.mSliderItems.add(EntryItem);
        notifyDataSetChanged();
    }

    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item, null);
        return new SliderAdapterVH(inflate);
    }

    public void onBindViewHolder(final SliderAdapterVH viewHolder, final int position) {

        final EntryItem EntryItem = mSliderItems.get(position);

//        viewHolder.textViewDescription.setText(EntryItem.getDescription());
//        viewHolder.textViewDescription.setTextSize(16);
//        viewHolder.textViewDescription.setTextColor(Color.WHITE);
//        Glide.with(viewHolder.itemView)
//                .load(EntryItem.getImageUrl())
//                .fitCenter()
//                .into(viewHolder.imageViewBackground);

        viewHolder.songName.setText(EntryItem.getName());
        Picasso.get().load(EntryItem.getCover()).into(viewHolder.songImage);;
//        viewHolder.itemCon.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(view.getContext(), DetailActivity.class);
//                intent.putExtra(EXTRA_MESSAGE, data.get(position).getName());
//                view.getContext().startActivity(intent);
//            }
//        }); ;

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openDetailPage(context, String.valueOf(EntryItem.getId()));
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
            View itemView;
            TextView songName;
            ImageView songImage;
            CardView itemCon;
            public SliderAdapterVH(View itemView) {
                super(itemView);
                songImage = (ImageView) itemView.findViewById(R.id.songImageView);
                songName = (TextView) itemView.findViewById(R.id.title);
                itemCon = (CardView) itemView.findViewById(R.id.songCard);
                this.itemView = itemView;
            }
        }
    }
