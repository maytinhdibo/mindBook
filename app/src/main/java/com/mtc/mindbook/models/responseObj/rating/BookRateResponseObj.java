package com.mtc.mindbook.models.responseObj.rating;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mtc.mindbook.models.responseObj.user.userData;

public class BookRateResponseObj {
    @SerializedName("message")
    @Expose
    private String messsage;

    public String getMesssage() {
        return messsage;
    }

}
