package com.quartzodev.api;

import android.util.Log;

import com.quartzodev.data.BookApi;
import com.quartzodev.data.BookResponse;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class APIServiceTest {

    private static final String TAG = APIServiceTest.class.getSimpleName();

    @Test
    public void shouldGetBooks() throws IOException, InterruptedException {

        Callback<BookResponse> callback = new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {

                //List<BookApi> bookList = response.body().items;


                Log.d(TAG, call.toString());
                this.notify();
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Log.e(TAG, call.toString());
                this.notify();
            }
        };

        APIService.getInstance().getBooks("flowers+inauthor:keyes", callback);

        synchronized (callback) {
            callback.wait();
        }


    }

}