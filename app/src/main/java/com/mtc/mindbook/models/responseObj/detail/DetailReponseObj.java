package com.mtc.mindbook.models.responseObj.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailReponseObj {

    @SerializedName("data")
    @Expose
    private List<Detail> data = null;

    public List<Detail> getData() {
        return data;
    }

    public void setData(List<Detail> data) {
        this.data = data;
    }
}
