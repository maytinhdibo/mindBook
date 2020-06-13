package com.mtc.mindbook.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.core.os.ConfigurationCompat;

import com.mtc.mindbook.DetailActivity;
import com.mtc.mindbook.PlaylistActivity;
import com.mtc.mindbook.SearchActivity;

import java.util.Locale;

public class Utils {
    public static void openDetailPage(Context context, String id) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("EXTRA_BOOK_ID", id);
        context.startActivity(intent);
    }

    public static void openSearchPage(Context context, String tag) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("EXTRA_TAG", tag);
        context.startActivity(intent);
    }

    public static Locale getLocale() {
        return ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
    }

    public static String getLocaleToString() {
        return getLocale().toString();
    }

    public static String convertDistance(float meter) {
        if (meter < 1000) {
            return String.format("%.1f", meter) + "m";
        } else {
            return String.format("%.1f", meter / 1000) + "km";
        }
    }

    public static void openPlaylistDetail(Context context, Integer playlistId, String title) {
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra("playlistId", playlistId);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }
}
