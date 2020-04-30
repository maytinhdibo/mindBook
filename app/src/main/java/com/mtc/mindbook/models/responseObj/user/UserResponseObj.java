package com.mtc.mindbook.models.responseObj.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponseObj {
    @SerializedName("data")
    @Expose
    private userData data;

    public userData getData() {
        return data;
    }

    public void setData(userData data) {
        this.data = data;
    }
}
