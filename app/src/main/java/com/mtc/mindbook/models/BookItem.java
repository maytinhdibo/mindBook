package com.mtc.mindbook.models;

public class BookItem {
    private int id;
    private String cover;
    private String name;
    private String authorName;
    private float rating;

    public BookItem(int id, String cover, String name) {
        this.id = id;
        this.cover = cover;
        this.name = name;
        this.rating = 0;
    }

    public BookItem(int id, String cover, String name, String authorName, float rating) {
        this.id = id;
        this.cover = cover;
        this.name = name;
        this.authorName = authorName;
        this.rating = rating;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }
}
