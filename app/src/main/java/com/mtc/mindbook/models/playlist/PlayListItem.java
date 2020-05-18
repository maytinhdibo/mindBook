package com.mtc.mindbook.models.playlist;

public class PlayListItem {
    private String name;
    private int bookCount;
    private String id;

    public PlayListItem(String name, int bookCount) {
        this.name = name;
        this.bookCount = bookCount;
        this.id = "0";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
