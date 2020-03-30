package com.mtc.mindbook.model.slideshow;

public class SlideShowItem {
    private int id;
    private String cover;
    private String name;

    public SlideShowItem(int id, String cover, String name) {
        this.id = id;
        this.cover = cover;
        this.name = name;
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

    public void setName(String name) {
        this.name = name;
    }
}
