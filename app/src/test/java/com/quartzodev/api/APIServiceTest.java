package com.quartzodev.api;

import android.util.Log;

import com.quartzodev.api.interfaces.IQuery;
import com.quartzodev.data.Book;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class APIServiceTest {

    private static final String TAG = APIServiceTest.class.getSimpleName();

    @Test
    public void shouldGetBookByISBNGoodreads() throws IOException, InterruptedException {


        IQuery query = APIService.getInstance().getService(APIService.GOODREADS);

        Book book = query.getBookByISBN("9781408276549");

    }

}