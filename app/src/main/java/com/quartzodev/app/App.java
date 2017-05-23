package com.quartzodev.app;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by victoraldir on 21/05/2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
