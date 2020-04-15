package com.mtc.mindbook.utils;
import android.content.Context;
import android.content.Intent;

import com.mtc.mindbook.DetailActivity;
import com.mtc.mindbook.SearchActivity;

import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;
import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;

public class Utils {
    public static void openDetailPage(Context context, String id){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        context.startActivity(intent);
    }
    public static void openSearchPage(Context context, String tag){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("EXTRA_TAG", tag);
        context.startActivity(intent);
    }
}
