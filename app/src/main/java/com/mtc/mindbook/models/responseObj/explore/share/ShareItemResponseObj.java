package com.mtc.mindbook.models.responseObj.explore.share;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShareItemResponseObj {

    @SerializedName("data")
    @Expose
    private List<ShareItem> data = null;

    public List<ShareItem> getData() {
        return data;
    }

    public void setData(List<ShareItem> data) {
        this.data = data;
    }
}
