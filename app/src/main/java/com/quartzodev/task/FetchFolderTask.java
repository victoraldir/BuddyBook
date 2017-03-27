package com.quartzodev.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.data.Folder;


/**
 * Created by victoraldir on 26/03/2017.
 */

public class FetchFolderTask extends AsyncTaskLoader<Folder> {

    private static final String TAG = FetchFolderTask.class.getSimpleName();

    public static final int FETCH_POPULAR_FOLDER = 0;
    public static final int FETCH_MY_BOOKS_FOLDER = 1;
    public static final int FETCH_CUSTOM_FOLDER = 2;


    private String popularMoviesFoderId = "-KgAV0L9l-T9N68ED9-b"; //See a better way to maintain popular folder
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String userId;
    private String folderName;
    private int operation;

    public FetchFolderTask(String userId, String folderName, Context context, int operation) {
        super(context);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");
        this.userId = userId;
        this.folderName = folderName;
        this.operation = operation;
    }

    @Override
    public Folder loadInBackground() {

        final Object locker = new Object();
        final Folder[] folder = new Folder[1];

        switch (operation){
            case FETCH_POPULAR_FOLDER:
                mDatabaseReference.orderByChild(popularMoviesFoderId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d(TAG,"Data received: " + dataSnapshot.toString());

                        folder[0] = dataSnapshot.child(popularMoviesFoderId).getValue(Folder.class);

                        synchronized (locker){
                            locker.notify();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.d(TAG,"Error: " + databaseError.getDetails());

                        synchronized (locker){
                            locker.notify();
                        }

                    }
                });

                synchronized (locker){
                    try {
                        locker.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case FETCH_MY_BOOKS_FOLDER:
                //mDatabaseReference.child(userId).orderByChild("myBooksFolder");
                break;
            case FETCH_CUSTOM_FOLDER:
                break;
        }



        return folder[0];

    }
}
