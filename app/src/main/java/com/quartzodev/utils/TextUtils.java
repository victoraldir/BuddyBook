package com.quartzodev.utils;


import com.quartzodev.data.Book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

}
