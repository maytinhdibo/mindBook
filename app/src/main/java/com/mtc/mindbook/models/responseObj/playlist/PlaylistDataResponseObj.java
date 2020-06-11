package com.mtc.mindbook.models.responseObj.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaylistDataResponseObj {
    @SerializedName("playlist_id")
    @Expose
    private Integer playlistId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("books_count")
    @Expose
    private Integer booksCount;

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(Integer booksCount) {
        this.booksCount = booksCount;
    }
}
