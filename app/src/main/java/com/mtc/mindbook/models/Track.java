package com.mtc.mindbook.models;

public class Track {
    private String title;
    private String bookTitle;
    private String author;
    private String mp3Link;
    private String coverLink;

    public Track() {
    }

    public Track(String title, String bookTitle, String author, String mp3Link, String coverLink) {
        this.title = title;
        this.bookTitle = bookTitle;
        this.author = author;
        this.mp3Link = mp3Link;
        this.coverLink = coverLink;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMp3Link() {
        return mp3Link;
    }

    public void setMp3Link(String mp3Link) {
        this.mp3Link = mp3Link;
    }

    public String getCoverLink() {
        return coverLink;
    }

    public void setCoverLink(String coverLink) {
        this.coverLink = coverLink;
    }
}
