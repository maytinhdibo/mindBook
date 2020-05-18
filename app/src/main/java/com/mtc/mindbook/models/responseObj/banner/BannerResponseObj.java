package com.mtc.mindbook.models.responseObj.banner;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerResponseObj {

    @SerializedName("data")
    @Expose
    private List<Banner> data = null;

    public List<Banner> getData() {
        return data;
    }

    public void setData(List<Banner> data) {
        this.data = data;
    }

}