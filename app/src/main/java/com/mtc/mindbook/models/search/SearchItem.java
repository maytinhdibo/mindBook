package com.mtc.mindbook.models.search;

public class SearchItem {
    private int id;
    private String cover;
    private String name;
    private String author;
    private double rating;

    public SearchItem(int id, String cover, String name, String author, double rating) {
        this.id = id;
        this.cover = cover;
        this.name = name;
        this.author = author;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
