package com.mtc.mindbook.models.responseObj;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Detail {
    @SerializedName("book_id")
    @Expose
    private Integer bookId;
    @SerializedName("book_title_vi")
    @Expose
    private String bookTitleVi;
    @SerializedName("book_title_en")
    @Expose
    private Object bookTitleEn;
    @SerializedName("book_description_vi")
    @Expose
    private String bookDescriptionVi;
    @SerializedName("book_description_en")
    @Expose
    private Object bookDescriptionEn;
    @SerializedName("book_cover")
    @Expose
    private String bookCover;
    @SerializedName("book_epub")
    @Expose
    private String bookEpub;
    @SerializedName("author")
    @Expose
    private Author author;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookTitleVi() {
        return bookTitleVi;
    }

    public void setBookTitleVi(String bookTitleVi) {
        this.bookTitleVi = bookTitleVi;
    }

    public Object getBookTitleEn() {
        return bookTitleEn;
    }

    public void setBookTitleEn(Object bookTitleEn) {
        this.bookTitleEn = bookTitleEn;
    }

    public String getBookDescriptionVi() {
        return bookDescriptionVi;
    }

    public void setBookDescriptionVi(String bookDescriptionVi) {
        this.bookDescriptionVi = bookDescriptionVi;
    }

    public Object getBookDescriptionEn() {
        return bookDescriptionEn;
    }

    public void setBookDescriptionEn(Object bookDescriptionEn) {
        this.bookDescriptionEn = bookDescriptionEn;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookEpub() {
        return bookEpub;
    }

    public void setBookEpub(String bookEpub) {
        this.bookEpub = bookEpub;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

}


