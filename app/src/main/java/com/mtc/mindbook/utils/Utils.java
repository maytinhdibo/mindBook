package com.mtc.mindbook.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.os.ConfigurationCompat;

import com.mtc.mindbook.DetailActivity;
import com.mtc.mindbook.LoginActivity;
import com.mtc.mindbook.R;
import com.mtc.mindbook.ReaderActivity;
import com.mtc.mindbook.SearchActivity;
import com.mtc.mindbook.models.responseObj.DefaultResponseObj;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;
import static com.mtc.mindbook.MainActivity.EXTRA_MESSAGE;

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

}
