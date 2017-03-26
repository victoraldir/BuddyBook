package com.quartzodev.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.quartzodev.api.APIService;
import com.quartzodev.data.Book;
import com.quartzodev.data.BookResponse;

import java.util.List;

import retrofit2.Response;

/**
 * Created by victoraldir on 26/03/2017.
 */

public class FetchMoviesTask extends AsyncTaskLoader<List<Book>> {


    public FetchMoviesTask(Context context) {
        super(context);
    }

    @Override
    public List<Book> loadInBackground() {

        List<Book> list = null;
//"":orderBy=newest
        try {
            Response<BookResponse> response = APIService.getInstance().getBooks("'':orderBy=relevance").execute();
            list = response.body().getItems();
        }catch (Exception ex){

        }

        return list;

    }
}
