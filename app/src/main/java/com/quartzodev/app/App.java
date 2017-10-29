package com.quartzodev.app;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;
import com.quartzodev.buddybook.BuildConfig;
import com.quartzodev.buddybook.R;
import com.quartzodev.utils.Constants;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by victoraldir on 21/05/2017.
 */

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_FREE))
            MobileAds.initialize(getBaseContext(),getString(R.string.ad_app_id));

        JodaTimeAndroid.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/baskvl.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
}
