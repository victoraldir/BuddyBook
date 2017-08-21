package com.quartzodev.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.quartzodev.buddybook.R;

/**
 * Created by victoraldir on 21/08/2017.
 */

public final class PrefUtils {

    private PrefUtils() {
    }

    public static int getSortMode(Context context) {
        String key = context.getString(R.string.pref_sort_key);
        int defaultValue = Integer.parseInt(context.getString(R.string.pref_sort_default));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defaultValue);
    }

    public static void setSortMode(Context context, int sortValue) {
        String key = context.getString(R.string.pref_sort_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, sortValue);

        editor.apply();
    }

}
