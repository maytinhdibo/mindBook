package com.mtc.soundme.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mtc.soundme.R;

import java.util.List;

public class EntryItem {
    private int id;
    private String cover;
    private String name;

    public EntryItem(int id, String cover, String name) {
        this.id = id;
        this.cover = cover;
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
