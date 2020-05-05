package com.mtc.mindbook.models.explore;

import com.mtc.mindbook.models.BookItemOld;

public class NearItem {
    private String comment;
    private String name;
    private float rating;
    private BookItemOld book;

    public NearItem(String comment, String name, float rating) {
        this.comment = comment;
        this.name = name;
        this.book=new BookItemOld(1,"","");
    }

    public NearItem(String comment, String name, BookItemOld book) {
        this.comment = comment;
        this.name = name;
        this.rating = rating;
        this.book = book;
    }

    public BookItemOld getBook() {
        return book;
    }

    public void setBook(BookItemOld book) {
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
