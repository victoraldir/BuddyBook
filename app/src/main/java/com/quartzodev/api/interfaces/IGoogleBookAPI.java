package com.quartzodev.api.interfaces;


import com.quartzodev.api.entities.google.BookResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by victoraldir on 14/06/2017.
 */

public interface IGoogleBookAPI {
    
    @GET("/books/v1/volumes")
    Call<BookResponse> getBooks(@Query("q") String query, @Query("key") String apiKey);

    @GET("/books/v1/volumes")
    Call<BookResponse> getBooksMaxResult(@Query("q") String query, @Query("maxResults") int maxResults, @Query("key") String apiKey);
}
