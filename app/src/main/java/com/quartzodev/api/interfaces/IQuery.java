package com.quartzodev.api.interfaces;

import com.quartzodev.data.Book;
import java.util.List;
import retrofit2.Callback;

/**
 * Created by victoraldir on 15/06/2017.
 */

public interface IQuery {

    List<Book> getBooks(String query);
    List<Book> getBooksMaxResult(String query, int maxResults);

    Book getBookByISBN(String isbn);

}
