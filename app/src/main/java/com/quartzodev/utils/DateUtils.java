package com.quartzodev.utils;

import android.text.format.DateFormat;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by victoraldir on 25/03/2017.
 */

public class DateUtils {

    private static final String DUBLIN_TIME_ZONE = "Europe/Dublin";
    private static final String MASK = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentTimeString(){

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeZone(TimeZone.getTimeZone(DUBLIN_TIME_ZONE));
        SimpleDateFormat sfd = new SimpleDateFormat(MASK, Locale.ENGLISH);

        return sfd.format(gc.getTime());
    }

}
