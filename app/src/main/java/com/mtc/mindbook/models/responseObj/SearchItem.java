package com.mtc.mindbook.models.responseObj;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchItem {
    @SerializedName("book_id")
    @Expose
    private Integer bookId;
    @SerializedName("book_title")
    @Expose
    private String bookTitle;
    @SerializedName("book_cover")
    @Expose
    private String bookCover;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("categories")
    @Expose
    private List<String> categories = null;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}


