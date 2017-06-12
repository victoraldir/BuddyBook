package com.quartzodev.utils;

import java.text.DateFormat;
import java.text.ParseException;
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
    private static final String MASK_PUBLISH_IN = "yyyy-MM-dd";
    private static final String MASK_PUBLISH_OUT = "MMMM dd, yyyy";

    public static String getCurrentTimeString() {

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeZone(TimeZone.getTimeZone(DUBLIN_TIME_ZONE));
        SimpleDateFormat sfd = new SimpleDateFormat(MASK, Locale.ENGLISH);

        return sfd.format(gc.getTime());
    }

    public static String formatStringDate(String date){

        String result = "";

        if (date == null) return result;

        DateFormat fmtOut = new SimpleDateFormat(MASK_PUBLISH_OUT, Locale.US);
        DateFormat fmtIn = new SimpleDateFormat(MASK_PUBLISH_IN, Locale.US);


        try {

            Date dataParsed = fmtIn.parse(date);

            result = fmtOut.format(dataParsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
}
