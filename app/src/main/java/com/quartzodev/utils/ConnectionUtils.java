package com.quartzodev.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by victoraldir on 24/05/2017.
 */

public class ConnectionUtils {

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
