package com.quartzodev.app;

import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.quartzodev.buddybook.BuildConfig;
import com.quartzodev.buddybook.R;
import com.quartzodev.utils.Constants;

import net.danlew.android.joda.JodaTimeAndroid;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import io.github.inflationx.calligraphy3.CalligraphyConfig;


/**
 * Created by victoraldir on 21/05/2017.
 */

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_FREE))
            MobileAds.initialize(getBaseContext(), getString(R.string.ad_app_id));

        JodaTimeAndroid.init(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
