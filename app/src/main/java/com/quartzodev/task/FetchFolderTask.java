package com.quartzodev.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;


/**
 * Created by victoraldir on 26/03/2017.
 */

public class FetchFolderTask extends AsyncTaskLoader<Folder> implements
        FirebaseDatabaseHelper.OnDataSnapshotListener,
        ChildEventListener {

    public static final int FETCH_POPULAR_FOLDER = 0;
    public static final int FETCH_MY_BOOKS_FOLDER = 1;
    public static final int FETCH_CUSTOM_FOLDER = 2;
    private static final String TAG = FetchFolderTask.class.getSimpleName();
    private static final int TIMEOUT = 15000;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private Folder mFolder;
    private String mUserId;
    private String mFolderId;
    private int operation;

    public FetchFolderTask(String userId, String folderId, Context context, int operation) {
        super(context);

        this.mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
        this.mUserId = userId;
        this.mFolderId = folderId;
        this.operation = operation;

    }

    @Override
    public Folder loadInBackground() {

        switch (operation) {
            case FETCH_POPULAR_FOLDER:

                Log.d(TAG, "Fetching FETCH_POPULAR_FOLDER..");

                mFirebaseDatabaseHelper.fetchPopularBooks(this);
                sleep();

                Log.d(TAG, "FETCH_POPULAR_FOLDER complete!");

                break;
            case FETCH_MY_BOOKS_FOLDER:

                Log.d(TAG, "Fetching FETCH_MY_BOOKS_FOLDER..");

                mFirebaseDatabaseHelper.fetchMyBooksFolder(mUserId, this);

                sleep();

                Log.d(TAG, "FETCH_MY_BOOKS_FOLDER complete!");

                break;
            case FETCH_CUSTOM_FOLDER:

                Log.d(TAG, "Fetching FETCH_CUSTOM_FOLDER..");

                mFirebaseDatabaseHelper.fetchBooksFromFolder(mUserId, mFolderId, this);

                sleep();

                Log.d(TAG, "FETCH_CUSTOM_FOLDER complete!");

                break;
        }

        return mFolder;

    }

    @Override
    public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
        Log.d(TAG, "Data received: " + dataSnapshot.toString());
        mFolder = dataSnapshot.getValue(Folder.class);
    }

    private void sleep() {
        synchronized (this) {
            try {
                Log.d(TAG, "Waiting " + TIMEOUT / 1000 + " seconds");
                this.wait(TIMEOUT);
            } catch (InterruptedException e) {
                Log.wtf(TAG, "No idea why");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
