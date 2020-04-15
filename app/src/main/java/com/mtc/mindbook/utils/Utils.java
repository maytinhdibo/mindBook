package com.mtc.mindbook.utils;
import android.content.Context;
import android.content.Intent;

import com.mtc.mindbook.DetailActivity;

import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;
import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;

public class Utils {
    public static void openDetailPage(Context context, String id){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        context.startActivity(intent);
    }
}
