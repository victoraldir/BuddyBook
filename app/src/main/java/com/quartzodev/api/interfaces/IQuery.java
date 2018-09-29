package com.quartzodev.api.interfaces;

import com.quartzodev.data.Book;

import java.util.List;

import androidx.annotation.Keep;

/**
 * Created by victoraldir on 15/06/2017.
 */

@Keep
public interface IQuery {

    List<Book> getBooks(String query);

    List<Book> getBooksMaxResult(String query, int maxResults);

    Book getBookByISBN(String isbn);

}
