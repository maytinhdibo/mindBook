package com.mtc.mindbook.models.responseObj;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookItem {
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
    private Integer rating;

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}


