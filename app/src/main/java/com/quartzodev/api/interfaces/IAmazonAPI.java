package com.quartzodev.api.interfaces;

import com.quartzodev.api.entities.amazon.ItemLookupResponse;
import com.quartzodev.api.entities.goodreads.GoodreadsResponse;

import androidx.annotation.Keep;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by victoraldir on 14/06/2017.
 */

@Keep
public interface IAmazonAPI {

    @GET
    Call<ItemLookupResponse> getBook(@Url String url);
}
