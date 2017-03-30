package com.quartzodev.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.task.FetchFolderTask;
import com.quartzodev.utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by victoraldir on 29/03/2017.
 */

public class FirebaseDatabaseHelper {

    private static final String TAG = FetchFolderTask.class.getSimpleName();

    private static final String ROOT = "users";
    private static final String REF_POPULAR_FOLDER = "-KgAV0L9l-T9N68ED9-b"; //See a better way to maintain popular folder
    private static final String REF_MY_BOOKS_FOLDER = "myBooksFolder";

    private DatabaseReference mDatabaseReference;

    private static FirebaseDatabaseHelper mInstance;


    private FirebaseDatabaseHelper(){
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ROOT);
    }

    public static FirebaseDatabaseHelper getInstance(){
        if(mInstance == null) mInstance = new FirebaseDatabaseHelper();

        return mInstance;
    }

    public void insertUser(User user){
        mDatabaseReference.child(user.getUid()).setValue(user);
    }

    public void updateUserLastActivity(String userId){
        Map<String, Object> mapLastActivity = new HashMap<>();
        mapLastActivity.put("lastActivity", DateUtils.getCurrentTimeString());
        updateUser(userId,mapLastActivity);
    }

    public void updateUser(String userId,  Map<String, Object> fields){
        mDatabaseReference.child(userId).updateChildren(fields);
    }

    public void fetchPopularBooks(final OnDataSnapshotListener onDataSnapshotListener){

        mDatabaseReference.child(REF_POPULAR_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));
    }

    public void fetchMyBooks(String userId, final OnDataSnapshotListener onDataSnapshotListener){

        mDatabaseReference.child(userId).child(REF_MY_BOOKS_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void fetchUserById(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    private ValueEventListener buildValueEventListener(final OnDataSnapshotListener onDataSnapshotListener){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataSnapshotListener.onDataSnapshotListenerAvailable(dataSnapshot);
                notifyCaller(onDataSnapshotListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO create method on OnDataSnapshotListener to deal with this
                Log.e(TAG,databaseError.getDetails());
            }
        };
    }

    private void notifyCaller(final OnDataSnapshotListener onDataSnapshotListener){
        synchronized (onDataSnapshotListener) {
            onDataSnapshotListener.notify(); //Release Task
        }
    }

    /**
     * This interface must be implemented by classes that want
     * to load DataSnapshot from this util
     */
    public interface OnDataSnapshotListener {
        void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot);
    }
}
