package com.quartzodev.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.buddybook.BuildConfig;
import com.quartzodev.utils.DateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by victoraldir on 29/03/2017.
 */

public class FirebaseDatabaseHelper {

    public static final String MAX_FOLDERS_KEY = "max_folders";
    public static final String MAX_BOOKS_KEY = "max_books";
    public static final String REF_POPULAR_FOLDER = "_popularBooks";
    public static final String REF_MY_BOOKS_FOLDER = "myBooksFolder";
    public static final String REF_SEARCH_HISTORY = "search_history";
    private static final String TAG = FirebaseDatabaseHelper.class.getSimpleName();
    private static final String ROOT = "users";
    private static final String REF_FOLDERS = "folders";
    private static final String REF_BOOKS = "books";
    private static FirebaseDatabaseHelper mInstance;
    private DatabaseReference mDatabaseReference;


    private FirebaseDatabaseHelper() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
        mDatabaseReference = mFirebaseDatabase.getReference().child(ROOT);
        mDatabaseReference.keepSynced(true);

    }

    public static FirebaseDatabaseHelper getInstance() {
        if (mInstance == null) mInstance = new FirebaseDatabaseHelper();

        return mInstance;
    }

    public void insertUser(User user, DatabaseReference.CompletionListener completionListener) {
        mDatabaseReference.updateChildren(Collections.singletonMap(user.getUid(), (Object) user), completionListener);
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

    public void fetchMyBooksFolder(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public ChildEventListener attachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).addChildEventListener(listener);
    }

    public void detachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).removeEventListener(listener);
    }

    public void fetchBooksFromFolder(String userId, String folderId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public ValueEventListener fetchLentBooks(String userId, final ValueEventListener valueEventListener) {

        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addValueEventListener(valueEventListener);

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

    public void findBookSearch(String userId, String folderId, String bookQuery, final OnDataSnapshotListener onDataSnapshotListener) {

        if (folderId == null) {

            mDatabaseReference.child(userId)
                    .child(REF_FOLDERS)
                    .child(REF_MY_BOOKS_FOLDER)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        } else {

            mDatabaseReference.child(userId)
                    .child(REF_FOLDERS)
                    .child(folderId)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        }

    }

    public void fetchFolders(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void attachFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addChildEventListener(listener);
    }

    public void detachFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.removeEventListener(listener);
    }


    public void deleteFolder(String userId, String folderId) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).removeValue();
    }

    public void insertFolder(final String userId, final Folder folder, final OnPaidOperationListener listener) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference df = mDatabaseReference.child(userId).child(REF_FOLDERS).push();
                folder.setId(df.getKey());
                folder.setCustom(true);
                df.setValue(folder);

                listener.onInsertFolder(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void insertDefaulFolder(String userId, String description, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference df = mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER);

        Folder myBooksFolder = new Folder();
        myBooksFolder.setId(UUID.randomUUID().toString());
        myBooksFolder.setDescription(description);
        myBooksFolder.setCustom(false);

        df.setValue(myBooksFolder, completionListener);

    }

    public void updateBook(String userId, String folderId, Book bookApi) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).updateChildren(Collections.singletonMap(bookApi.getId(), (Object) bookApi));
    }

    public void insertBookFolder(String userId, String folderId, final Book bookApi, final OnPaidOperationListener listener) {

        final DatabaseReference ref = mDatabaseReference
                .child(userId)
                .child(REF_FOLDERS)
                .child(folderId)
                .child("books");


        if(bookApi.getId() == null && bookApi.getIdProvider() != null){ //If it's search book

            ref.orderByChild("idProvider")
                    .equalTo(bookApi.getIdProvider())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() == null){
                                insert(dataSnapshot,listener,bookApi,ref);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
        } else {

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    insert(dataSnapshot, listener, bookApi, ref);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }

    }

    public void insertBookPopularFolder(final Book book) {

        DatabaseReference ref = mDatabaseReference.child(REF_POPULAR_FOLDER).push();

        book.setId(ref.getKey());

        ref.setValue(book);

    }

    private void insert(DataSnapshot dataSnapshot,OnPaidOperationListener listener, final Book bookApi, DatabaseReference ref){
        Log.d(TAG, "Folder list is: " + dataSnapshot.getChildrenCount());

        if(bookApi.getId() == null ){ //Here we generate our id
            bookApi.setId(ref.push().getKey());
        }

        ref.updateChildren(Collections.singletonMap(bookApi.getId(), (Object) bookApi));
        listener.onInsertBook(true);
    }

    public void deleteBookFolder(String userId, String folderId, Book bookApi) {
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
                try {
                    onDataSnapshotListener.onDataSnapshotListenerAvailable(dataSnapshot);
                    notifyCaller(onDataSnapshotListener);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
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

    public interface OnPaidOperationListener {
        void onInsertBook(boolean success); //Return true if insertion was valid

        void onInsertFolder(boolean success);
    }
}
