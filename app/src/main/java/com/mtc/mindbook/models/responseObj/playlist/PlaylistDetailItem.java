package com.mtc.mindbook.models.responseObj.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistDetailItem {

    @SerializedName("book_id")
    @Expose
    private String bookId;
    @SerializedName("book_title")
    @Expose
    private String bookTitle;
    @SerializedName("book_cover")
    @Expose
    private String bookCover;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("audio")
    @Expose
    private List<String> audio = null;
    @SerializedName("categories")
    @Expose
    private List<String> categories = null;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getAudio() {
        return audio;
    }

    public void setAudio(List<String> audio) {
        this.audio = audio;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
