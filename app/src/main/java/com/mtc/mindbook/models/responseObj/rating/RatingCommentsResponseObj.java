package com.mtc.mindbook.models.responseObj.rating;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingCommentsResponseObj {

    @SerializedName("data")
    @Expose
    private List<RatingComment> data = null;

    public List<RatingComment> getData() {
        return data;
    }

    public void setData(List<RatingComment> data) {
        this.data = data;
    }

}