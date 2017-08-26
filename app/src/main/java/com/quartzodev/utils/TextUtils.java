package com.quartzodev.utils;


import com.quartzodev.data.Book;

/**
 * Created by victoraldir on 26/08/2017.
 */

public class TextUtils {

    public static String getFirstLetterTitle(Book book){

        char result = '#';

        try{
            result = book.getVolumeInfo().getTitle().trim().charAt(0);
        }catch (Exception ex){

        }

        return  Character.toString(result);
    }

}
