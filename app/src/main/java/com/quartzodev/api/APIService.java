package com.quartzodev.api;


import com.quartzodev.api.interfaces.IGoodreadsAPI;
import com.quartzodev.api.interfaces.IGoogleBookAPI;
import com.quartzodev.api.interfaces.IQuery;
import com.quartzodev.api.strategies.GoodreadsImpl;
import com.quartzodev.api.strategies.GoogleImpl;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class APIService {

    public static final int GOOGLE = 1;
    public static final int GOODREADS = 2;

    private static final String GOOGLE_API_URL = "https://www.googleapis.com";
    private static final String GOODREADS_API_URL = "https://www.goodreads.com";
    private static APIService sMinstance;
    private IGoogleBookAPI mGoogleService;
    private IGoodreadsAPI mGoodreadsService;

    private APIService() {
        initService();
    }

    private void initService() {
        // Add the interceptor to OkHttpClient
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder
                .addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        //Google service
        Retrofit retrofitGoogle = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(GOOGLE_API_URL)
                .client(client)
                .build();

        mGoogleService = retrofitGoogle.create(IGoogleBookAPI.class);

        //Goodreads service
        Retrofit retrofitGoodReads = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl(GOODREADS_API_URL)
                .client(client)
                .build();

        mGoodreadsService = retrofitGoodReads.create(IGoodreadsAPI.class);
    }

    public static APIService getInstance() {

        if (sMinstance == null) sMinstance = new APIService();

        return sMinstance;
    }


    public IQuery getService(int typeService) {

        switch (typeService) {
            case GOOGLE:
                return new GoogleImpl(mGoogleService);
            case GOODREADS:
                return new GoodreadsImpl(mGoodreadsService);
            default:
                return null;

        }

    }

}
