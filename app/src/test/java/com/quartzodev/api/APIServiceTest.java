package com.quartzodev.api;

import com.quartzodev.api.interfaces.IQuery;
import com.quartzodev.data.Book;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by victoraldir on 24/03/2017.
 */
@Ignore
public class APIServiceTest {


    @Test
    public void shouldFindBookByISBNGoogle() {

        IQuery query = APIService.getInstance().getService(APIService.GOOGLE);
        Book book = query.getBookByISBN("9781133709077");

        Assert.assertNotNull(book);

        book = query.getBookByISBN("1133709079-");

        Assert.assertNull(book);
    }

    @Test
    public void shouldFindBookByISBNGoodreads() {

        IQuery query = APIService.getInstance().getService(APIService.GOODREADS);

        Book book = query.getBookByISBN("9780545010221");

        Assert.assertNotNull(book);

        book = query.getBookByISBN("1133709079-");

        Assert.assertNull(book);

    }

}

