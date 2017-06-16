package com.quartzodev.api.entities.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by victoraldir on 14/06/2017.
 */

@Root(strict = false)
public class GoodreadsResponse {

    @Element
    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
