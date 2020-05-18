package com.mtc.mindbook.models.responseObj.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mtc.mindbook.MainActivity;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class getUser {
    private static APIService userService = null;

    public getUser(Context context) {
        userService = APIUtils.getUserService();
        SharedPreferences sharedPrefs = context.getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
        String accessToken = sharedPrefs.getString("accessToken", "");
        Call<UserResponseObj> callDetail = userService.fetchUserInfo("Bearer " + accessToken);
        callDetail.enqueue(new Callback<UserResponseObj>() {
            @Override
            public void onResponse(Call<UserResponseObj> call, Response<UserResponseObj> response) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("userEmail", response.body().getData().getEmail());
                editor.putString("userFullName", response.body().getData().getFirstName() + " " + response.body().getData().getLastName());
                editor.putString("userName", response.body().getData().getUserName());
                editor.apply();
                Intent intent = new Intent(context, MainActivity.class);
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                ((Activity) context).finish();
            }

            @Override
            public void onFailure(Call<UserResponseObj> call, Throwable t) {

            }
        });
    }
}
