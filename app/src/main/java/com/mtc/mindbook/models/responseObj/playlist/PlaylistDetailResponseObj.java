package com.mtc.mindbook.models.responseObj.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistDetailResponseObj {

    @SerializedName("data")
    @Expose
    private List<PlaylistDetailItem> data = null;

    public List<PlaylistDetailItem> getData() {
        return data;
    }

    public void setData(List<PlaylistDetailItem> data) {
        this.data = data;
    }

}
