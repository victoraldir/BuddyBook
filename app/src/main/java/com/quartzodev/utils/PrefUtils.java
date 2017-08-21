package com.quartzodev.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.quartzodev.buddybook.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by victoraldir on 21/08/2017.
 */

public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getSorts(Context context) {

        String sortKey = context.getString(R.string.pref_sort_key);
        String initializedKey = context.getString(R.string.pref_sort_initialized_key);
        String[] defaultSortsList = context.getResources().getStringArray(R.array.default_sorts);

        HashSet<String> defaultSorts = new HashSet<>(Arrays.asList(defaultSortsList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(sortKey, defaultSorts);
            editor.apply();
            return defaultSorts;
        }
        return prefs.getStringSet(sortKey, new HashSet<String>());

    }

    private static void editSortPref(Context context, String sort, Boolean add) {
        String key = context.getString(R.string.pref_sort_key);
        Set<String> sorts = getSorts(context);

        if (add) {
            sorts.add(sort);
        } else {
            sorts.remove(sort);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, sorts);
        editor.apply();
    }

    public static void addSort(Context context, String symbol) {
        editSortPref(context, symbol, true);
    }

    public static void removeSort(Context context, String symbol) {
        editSortPref(context, symbol, false);
    }

    public static String getSortMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void setSortMode(Context context, int stringId) {
        String key = context.getString(R.string.pref_display_mode_key);
        String sortValue = context.getString(stringId);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, sortValue);

        editor.apply();
    }

}
