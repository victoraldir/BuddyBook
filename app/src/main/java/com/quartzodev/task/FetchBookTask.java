package com.quartzodev.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.quartzodev.api.BookApi;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;

import java.util.List;

/**
 * Created by victoraldir on 12/04/2017.
 */

public class FetchBookTask extends AsyncTaskLoader<BookApi> implements
        FirebaseDatabaseHelper.OnDataSnapshotListener{

    private static final String TAG = FetchBookTask.class.getSimpleName();

    private static final int TIMEOUT = 15000;

    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private String mFolderId;
    private String mBookId;
    private String mUserId;
    private BookApi mBook;

    public FetchBookTask(Context context, String userId, String folderId, String bookId) {
        super(context);

        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        mFolderId = folderId;
        mBookId = bookId;
        mUserId = userId;
    }

    @Override
    public BookApi loadInBackground() {

        mFirebaseDatabaseHelper.findBook(mUserId,mFolderId,mBookId,this);

        sleep();

        return mBook;
    }

    private void sleep(){
        synchronized (this){
            try {
                Log.d(TAG,"Waiting " + TIMEOUT / 1000 + " seconds");
                this.wait(TIMEOUT);
            } catch (InterruptedException e) {
                Log.wtf(TAG,"No idea why");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
        Log.d(TAG,"Data received: " + dataSnapshot.toString());
        mBook = dataSnapshot.getValue(BookApi.class);
    }

}
