package com.mtc.mindbook.models.responseObj.explore.near;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyResponseObj {
    @SerializedName("data")
    @Expose
    private List<NearbyItem> data = null;

    public List<NearbyItem> getData() {
        return data;
    }

    public void setData(List<NearbyItem> data) {
        this.data = data;
    }
}
