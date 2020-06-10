package com.mtc.mindbook.models.responseObj.explore.near;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mtc.mindbook.models.responseObj.detail.BookDetail;
import com.mtc.mindbook.models.responseObj.user.UserData;

public class NearbyItem {

    @SerializedName("user")
    @Expose
    private UserData user;
    @SerializedName("distance")
    @Expose
    private float distance;
    @SerializedName("book_detail")
    @Expose
    private BookDetail bookDetail;

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public BookDetail getBookDetail() {
        return bookDetail;
    }

    public void setBookDetail(BookDetail bookDetail) {
        this.bookDetail = bookDetail;
    }

}