package com.quartzodev.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
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
    private static final String ROOT = "users";
    private static final String REF_FOLDERS = "folders";
    private static final String REF_BOOKS = "books";
    private static FirebaseDatabaseHelper mInstance;
    private DatabaseReference mDatabaseReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private long mMaxFolders;
    private long mMaxBooks;


    private FirebaseDatabaseHelper() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
        mDatabaseReference = mFirebaseDatabase.getReference().child(ROOT);
        mDatabaseReference.keepSynced(true);

        initRemoteConfig();

    }

    public static FirebaseDatabaseHelper getInstance() {
        if (mInstance == null) mInstance = new FirebaseDatabaseHelper();

        return mInstance;
    }

    private void initRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        fetchRemoteConfig();
    }

    public void fetchRemoteConfig() {

        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedConfig();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                applyRetrievedConfig();
            }
        });
    }

    public void applyRetrievedConfig() {
        mMaxFolders = mFirebaseRemoteConfig.getLong(MAX_FOLDERS_KEY);
        mMaxBooks = mFirebaseRemoteConfig.getLong(MAX_BOOKS_KEY);
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

    public void fetchPopularBooks(final ValueEventListener valueEventListener) {

        mDatabaseReference.child(REF_POPULAR_FOLDER)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public ChildEventListener attachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).addChildEventListener(listener);
    }

    public void detachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).removeEventListener(listener);
    }

    public void fetchBooksFromFolder(String userId, String folderId, String sort, final ValueEventListener valueEventListener) {

        mDatabaseReference
                .child(userId)
                .child(REF_FOLDERS)
                .child(folderId)
                .child(REF_BOOKS)
                .orderByChild("volumeInfo/"+sort)
                .addListenerForSingleValueEvent(valueEventListener);

    }

    public ValueEventListener fetchLentBooks(String userId, final ValueEventListener valueEventListener) {

        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addValueEventListener(valueEventListener);

    }

    public void findBookSearch(String userId, String folderId, final ValueEventListener valueEventListener) {

        if (folderId == null) {

            mDatabaseReference.child(userId)
                    .child(REF_FOLDERS)
                    .child(REF_MY_BOOKS_FOLDER)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
                    .addListenerForSingleValueEvent(valueEventListener);

        } else {

            mDatabaseReference.child(userId)
                    .child(REF_FOLDERS)
                    .child(folderId)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
                    .addListenerForSingleValueEvent(valueEventListener);

        }

    }

    public void fetchFolders(String userId, final ValueEventListener valueEventListener) {

        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addListenerForSingleValueEvent(valueEventListener);

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

                if (dataSnapshot.getChildrenCount() >= mMaxFolders) {

                    listener.onInsertFolder(false);

                } else {

                    DatabaseReference df = mDatabaseReference.child(userId).child(REF_FOLDERS).push();
                    folder.setId(df.getKey());
                    folder.setCustom(true);
                    df.setValue(folder);

                    listener.onInsertFolder(true);

                }

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

    public void findBook(String userId, String folderId, String bookId, ValueEventListener valueEventListener){
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(bookId).addListenerForSingleValueEvent(valueEventListener);
    }

    public void updateBookAnnotation(String userId, String folderId, String bookId, String annotation){

        Map<String, Object> mapAnnotation = new HashMap<>();
        mapAnnotation.put("annotation", annotation);

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(bookId).updateChildren(mapAnnotation);
    }

    private void insert(DataSnapshot dataSnapshot,OnPaidOperationListener listener, final Book bookApi, DatabaseReference ref){

        if (dataSnapshot.getChildrenCount() >= mMaxBooks) {
            listener.onInsertBook(false);
        } else {

            if(bookApi.getId() == null ){ //Here we generate our id
                bookApi.setId(ref.push().getKey());
            }

            ref.updateChildren(Collections.singletonMap(bookApi.getId(), (Object) bookApi));
            listener.onInsertBook(true);
        }
    }

    public void deleteBookFolder(String userId, String folderId, Book bookApi) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(bookApi.getId()).removeValue();
    }

    public void fetchUserById(String userId, final ValueEventListener valueEventListener) {

        mDatabaseReference.child(userId)
                .addListenerForSingleValueEvent(valueEventListener);

    }

    public interface OnPaidOperationListener {
        void onInsertBook(boolean success); //Return true if insertion was valid

        void onInsertFolder(boolean success);
    }
}
