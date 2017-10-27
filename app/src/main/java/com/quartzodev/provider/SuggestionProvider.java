package com.quartzodev.provider;

import android.content.SearchRecentSuggestionsProvider;

import com.quartzodev.buddybook.BuildConfig;

/**
 * Created by victoraldir on 26/03/2017.
 */

public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
