package com.quartzodev.utils;

import org.junit.Test;

import java.util.TimeZone;

/**
 * Created by victoraldir on 25/03/2017.
 */
public class DateUtilsTest {

    @Test
    public void shouldPrintTimeZone() {

        for (int x = 0; x < TimeZone.getAvailableIDs().length; x++) {
            System.out.println(TimeZone.getAvailableIDs()[x]);
        }

    }

    @Test
    public void shouldPrintDublinTime() {
        System.out.print(DateUtils.getCurrentTimeString());
    }

}