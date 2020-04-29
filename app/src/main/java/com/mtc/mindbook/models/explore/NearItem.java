package com.mtc.mindbook.models.explore;

import com.mtc.mindbook.models.BookItem;

public class NearItem {
    private String comment;
    private String name;
    private float rating;
    private BookItem book;

    public NearItem(String comment, String name, float rating) {
        this.comment = comment;
        this.name = name;
        this.book=new BookItem(1,"","");
    }

    public NearItem(String comment, String name, BookItem book) {
        this.comment = comment;
        this.name = name;
        this.rating = rating;
        this.book = book;
    }

    public BookItem getBook() {
        return book;
    }

    public void setBook(BookItem book) {
        this.book = book;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
