package com.mtc.mindbook.models.responseObj.catetory.trending;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mtc.mindbook.models.responseObj.BookItem;

import java.util.List;

public class BookTrendResponseObj {

    @SerializedName("data")
    @Expose
    private List<BookItem> data = null;

    public List<BookItem> getData() {
        return data;
    }

    public void setData(List<BookItem> data) {
        this.data = data;
    }
}
