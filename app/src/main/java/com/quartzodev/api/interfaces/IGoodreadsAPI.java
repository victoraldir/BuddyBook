package com.quartzodev.api.interfaces;

import android.support.annotation.Keep;

import com.quartzodev.api.entities.goodreads.GoodreadsResponse;
import com.quartzodev.api.entities.google.BookResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by victoraldir on 14/06/2017.
 */

@Keep
public interface IGoodreadsAPI {
    @GET("/book/isbn")
    Call<GoodreadsResponse> findBookByISBN(@Query("isbn") String isbn, @Query("key") String apiKey);

}
