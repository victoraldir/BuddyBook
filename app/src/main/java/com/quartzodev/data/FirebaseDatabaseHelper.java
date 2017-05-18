package com.quartzodev.data;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.api.BookApi;
import com.quartzodev.task.FetchFolderTask;
import com.quartzodev.utils.DateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by victoraldir on 29/03/2017.
 */

public class FirebaseDatabaseHelper {

    public static final String REF_POPULAR_FOLDER = "_popularBooks"; //See a better way to maintain popular folder
    public static final String REF_MY_BOOKS_FOLDER = "myBooksFolder";
    public static final String REF_SEARCH_HISTORY = "search_history"; //See a better way to maintain popular folder
    private static final String TAG = FetchFolderTask.class.getSimpleName();
    private static final String ROOT = "users";
    private static final String REF_FOLDERS = "folders";
    private static final String REF_BOOKS = "books";
    private static FirebaseDatabaseHelper mInstance;
    private DatabaseReference mDatabaseReference;


    private FirebaseDatabaseHelper() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
        mDatabaseReference = mFirebaseDatabase.getReference().child(ROOT);
    }

    public static FirebaseDatabaseHelper getInstance() {
        if (mInstance == null) mInstance = new FirebaseDatabaseHelper();

        return mInstance;
    }

    public void insertUser(User user) {
        mDatabaseReference.child(user.getUid()).setValue(user);
    }

    public void updateUserLastActivity(String userId) {
        Map<String, Object> mapLastActivity = new HashMap<>();
        mapLastActivity.put("lastActivity", DateUtils.getCurrentTimeString());
        updateUser(userId, mapLastActivity);
    }

    public void updateUser(String userId, Map<String, Object> fields) {
        mDatabaseReference.child(userId).updateChildren(fields);
    }

    public void fetchPopularBooks(final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(REF_POPULAR_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));
    }

    public void insertPopularBooks(Map<String, BookApi> books) {

        Folder folder = new Folder("Popular Books Folder");

        folder.setBooks(books);

        mDatabaseReference.child(REF_POPULAR_FOLDER).setValue(folder);

    }

    public void insertBookSearchHistory(String userId, BookApi book) {

        mDatabaseReference.child(userId).child(REF_SEARCH_HISTORY).updateChildren(Collections.singletonMap(book.getId(), (Object) book));

    }

    public void fetchMyBooksFolder(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public ChildEventListener attachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener){
        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).addChildEventListener(listener);
    }

    public void detachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener){
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).removeEventListener(listener);
    }

    public void fetchBooksFromFolder(String userId, String folderId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void findBook(String userId, String folderId, String bookId, final OnDataSnapshotListener onDataSnapshotListener) {

        if (folderId == null) {

            mDatabaseReference.child(userId).child(REF_SEARCH_HISTORY).child(bookId).addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        } else if (folderId.equals(REF_POPULAR_FOLDER)) {

            mDatabaseReference.child(REF_POPULAR_FOLDER).child("books").child(bookId)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        } else {

            mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child("books").child(bookId)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        }


    }

    public void fetchFolders(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        //ref.addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        ref.orderByChild("custom")
                .equalTo(true)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void attachFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addChildEventListener(listener);
    }

    public void detacheFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.removeEventListener(listener);
    }


    public void deleteFolder(String userId, String folderId) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).removeValue();
    }

    public void insertFolder(String userId, Folder folder) {
        DatabaseReference df = mDatabaseReference.child(userId).child(REF_FOLDERS).push();

        folder.setId(df.getKey());

        folder.setCustom(true);

        df.setValue(folder);
    }

    public void insertBookFolder(String userId, String folderId, BookApi bookApi) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child("books").updateChildren(Collections.singletonMap(bookApi.getId(), (Object) bookApi));
    }

    public void deleteBookFolder(String userId, String folderId, BookApi bookApi) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(bookApi.getId()).removeValue();
    }

    public void fetchUserById(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    private ValueEventListener buildValueEventListener(final OnDataSnapshotListener onDataSnapshotListener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataSnapshotListener.onDataSnapshotListenerAvailable(dataSnapshot);
                notifyCaller(onDataSnapshotListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO create method on OnDataSnapshotListener to deal with this
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    private void notifyCaller(final OnDataSnapshotListener onDataSnapshotListener) {
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
