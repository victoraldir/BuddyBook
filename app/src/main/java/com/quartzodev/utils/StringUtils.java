package com.quartzodev.utils;

/**
 * Created by victoraldir on 10/03/2018.
 */

public class StringUtils {

    public static boolean isIsbn(String isbn){
        if(isbn.length() == 10 || isbn.length() == 13){
            return true;
        }

        return false;
    }

}
