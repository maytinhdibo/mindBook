package com.mtc.mindbook.models.responseObj.rating;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingComment {
    @SerializedName("rating_num")
    @Expose
    private int ratingNum;
    @SerializedName("rating_comment")
    @Expose
    private String ratingComment;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    public int getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(int ratingNum) {
        this.ratingNum = ratingNum;
    }

    public String getRatingComment() {
        return ratingComment;
    }

    public void setRatingComment(String ratingComment) {
        this.ratingComment = ratingComment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}