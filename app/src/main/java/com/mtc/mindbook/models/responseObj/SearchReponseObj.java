package com.mtc.mindbook.models.responseObj;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchReponseObj {

    @SerializedName("data")
    @Expose
    private List<Search> data = null;

    public List<Search> getData() {
        return data;
    }

    public void setData(List<Search> data) {
        this.data = data;
    }
}
