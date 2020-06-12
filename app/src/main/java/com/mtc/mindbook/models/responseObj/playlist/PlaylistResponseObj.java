package com.mtc.mindbook.models.responseObj.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistResponseObj {
    @SerializedName("data")
    @Expose
    private List<PlaylistDataResponseObj> data = null;

    public List<PlaylistDataResponseObj> getData() {
        return data;
    }

    public void setData(List<PlaylistDataResponseObj> data) {
        this.data = data;
    }
}
