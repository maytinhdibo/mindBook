package com.mtc.mindbook.models.responseObj.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Banner {
    @SerializedName("book_id")
    @Expose
    private Integer id;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
