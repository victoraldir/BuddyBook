package com.quartzodev.buddybook;

/**
 * Created by victoraldir on 14/08/2017.
 */

public class AbstractTest {

    public void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
