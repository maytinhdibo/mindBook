package com.mtc.mindbook.remote;

public class APIUtils {

    public static APIService getUserService(){
        return RequestClient.getClient().create(APIService.class);
    }
}
