package com.quartzodev.api;

import com.quartzodev.buddybook.BuildConfig;
import com.quartzodev.data.BookResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class APIService {

    private static final String API_URL = "https://www.googleapis.com";
    private static APIService sMinstance;
    private IGoogleBookAPI mService;

    private APIService() {

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //builder.addNetworkInterceptor(new StethoInterceptor());
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(API_URL)
                .client(client)
                .build();

        mService = retrofit.create(IGoogleBookAPI.class);

    }

    public static APIService getInstance() {

        if (sMinstance == null) sMinstance = new APIService();

        return sMinstance;
    }

    public void getBooks(String query, Callback<BookResponse> callback) {

        mService.getBooks(query, BuildConfig.GOOGLE_BOOK_API_KEY).enqueue(callback);

    }

    public Call<BookResponse> getBooks(String query) {
        return mService.getBooks(query, BuildConfig.GOOGLE_BOOK_API_KEY);
    }

    public Call<BookResponse> getBooksMaxResult(String query, int maxResults) {
        return mService.getBooksMaxResult(query, maxResults, BuildConfig.GOOGLE_BOOK_API_KEY);
    }

    interface IGoogleBookAPI {
        @GET("/books/v1/volumes")
        Call<BookResponse> getBooks(@Query("q") String query, @Query("key") String apiKey);

        @GET("/books/v1/volumes")
        Call<BookResponse> getBooksMaxResult(@Query("q") String query, @Query("maxResults") int maxResults, @Query("key") String apiKey);
    }
}
