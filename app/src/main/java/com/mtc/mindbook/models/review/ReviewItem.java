package com.mtc.mindbook.models.review;

public class ReviewItem {
    private String comment;
    private String avt;
    private String name;
    private float rating;

    public ReviewItem(String comment, String avt, String name, float rating) {
        this.comment = comment;
        this.avt = avt;
        this.name = name;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
