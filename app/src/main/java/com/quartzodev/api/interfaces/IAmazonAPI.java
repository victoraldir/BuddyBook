package com.quartzodev.api.interfaces;

import androidx.annotation.Keep;

import com.quartzodev.api.entities.amazon.ItemLookupResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by victoraldir on 14/06/2017.
 */

@Keep
public interface IAmazonAPI {

    @GET
    Call<ItemLookupResponse> getBook(@Url String url);
}
