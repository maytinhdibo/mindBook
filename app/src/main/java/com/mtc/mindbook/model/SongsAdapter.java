package com.mtc.mindbook.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtc.mindbook.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongsAdapter extends ArrayAdapter<EntryItem> {

    public SongsAdapter(Context context, List<EntryItem> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        EntryItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.title);
        ImageView tvImage = (ImageView) convertView.findViewById(R.id.songImageView);
        // Populate the data into the template view using the data object
        tvName.setText(item.getName());
        Picasso.get().load(item.getCover()).into(tvImage);
        // Return the completed view to render on screen
        return convertView;
    }
}