package com.quartzodev.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.quartzodev.api.APIService;
import com.quartzodev.data.BookApi;
import com.quartzodev.data.BookResponse;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

/**
 * Created by victoraldir on 14/05/2017.
 */

public class SearchTask extends AsyncTaskLoader<List<BookApi>> {

    private final String LOG = SearchTask.class.getSimpleName();
    private boolean mCanceled = false;
    private String mQuery;
    private Integer mMaxResult;
    private List<BookApi> mData;

    public SearchTask(Context context) {
        super(context);
    }

    public SearchTask(Context context, String query, Integer maxResult) {
        super(context);
        mQuery = query;
        mMaxResult = maxResult;
    }


    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Use cached data
            deliverResult(mData);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<BookApi> data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected boolean onCancelLoad() {
        Log.d(LOG, "onCancelLoad() fired!!!");
        return super.onCancelLoad();
    }

    @Override
    public List<BookApi> loadInBackground() {

        if(mQuery == null) return null;

        try {

            Response<BookResponse> bookResponseResponse;

            try {
                Thread.sleep(500); //Just to avoid unnecessary queries
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isReset() || isLoadInBackgroundCanceled() || isAbandoned()) {
                Log.d(LOG, "This operation should be cancelled");
                Log.d(LOG, "isReset() : " + isReset());
                Log.d(LOG, "isAbandoned() : " + isAbandoned());
                Log.d(LOG, "isLoadInBackgroundCanceled() : " + isLoadInBackgroundCanceled());
                return null;
            }

            Log.d(LOG, "Thread ID: " + getId() + ". Running search query for text: " + mQuery);
            //Has max results limit
            if (mMaxResult != null && mMaxResult != 0) {
                bookResponseResponse = APIService.getInstance()
                        .getBooksMaxResult(mQuery, mMaxResult)
                        .execute();
            } else {
                bookResponseResponse = APIService.getInstance().getBooks(mQuery).execute(); //Blocks!
            }


            if (bookResponseResponse.body() != null && !mCanceled) {
                return bookResponseResponse.body().getItems();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
