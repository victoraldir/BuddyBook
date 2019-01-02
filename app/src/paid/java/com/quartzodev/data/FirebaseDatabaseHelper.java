package com.quartzodev.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.utils.DateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by victoraldir on 29/03/2017.
 */

public class FirebaseDatabaseHelper {

    public static final String REF_POPULAR_FOLDER = "_popularBooks";
    public static final String REF_MY_BOOKS_FOLDER = "myBooksFolder";
    private static final String ROOT = "users";
    private static final String REF_FOLDERS = "folders";
    private static final String REF_BOOKS = "books";
    private static FirebaseDatabaseHelper mInstance;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;


    private FirebaseDatabaseHelper() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
        mDatabaseReference = mFirebaseDatabase.getReference().child(ROOT);
        mDatabaseReference.keepSynced(true);

        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    private String getUserId(){
        if(mFirebaseAuth.getCurrentUser() != null)
            return mFirebaseAuth.getCurrentUser().getUid();

        return "";
    }

    public static FirebaseDatabaseHelper getInstance() {
        if (mInstance == null) mInstance = new FirebaseDatabaseHelper();

        return mInstance;
    }

    public void insertUser(User user, DatabaseReference.CompletionListener completionListener) {
        mDatabaseReference.updateChildren(Collections.singletonMap(user.getUid(), (Object) user), completionListener);
    }

    public void updateUserLastActivity() {
        Map<String, Object> mapLastActivity = new HashMap<>();
        mapLastActivity.put("lastActivity", DateUtils.getCurrentTimeString());
        updateUser(getUserId(), mapLastActivity);
    }

    public void updateUser(String userId, Map<String, Object> fields) {
        mDatabaseReference.child(userId).updateChildren(fields);
    }


    public void fetchPopularBooks(final ValueEventListener valueEventListener) {

        mDatabaseReference.child(REF_POPULAR_FOLDER)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public Query fetchPopularBooks() {
        return mDatabaseReference.child(REF_POPULAR_FOLDER);
    }

    public DatabaseReference getDatabaseRef() {
        return mDatabaseReference;
    }

    public ChildEventListener attachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).addChildEventListener(listener);
    }

    public void detachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).removeEventListener(listener);
    }

    public void fetchBooksFromFolder(String folderId, String sort, final ValueEventListener valueEventListener) {

        mDatabaseReference
                .child(getUserId())
                .child(REF_FOLDERS)
                .child(folderId)
                .child(REF_BOOKS)
                .orderByChild("volumeInfo/" + sort)
                .addListenerForSingleValueEvent(valueEventListener);

    }

    public Query fetchBooksFromFolder(String folderId, String sort) {
        return mDatabaseReference
                .getRef()
                .child(getUserId())
                .child(REF_FOLDERS)
                .child(folderId)
                .child(REF_BOOKS)
                .orderByChild("volumeInfo/" + sort);
    }

    public ValueEventListener fetchLentBooks(final ValueEventListener valueEventListener) {

        return mDatabaseReference.child(getUserId()).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addValueEventListener(valueEventListener);

    }

    public void findBookSearch(String folderId, final ValueEventListener valueEventListener) {

        if (folderId == null) {

            mDatabaseReference.child(getUserId())
                    .child(REF_FOLDERS)
                    .child(REF_MY_BOOKS_FOLDER)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
                    .addListenerForSingleValueEvent(valueEventListener);

        } else {

            mDatabaseReference.child(getUserId())
                    .child(REF_FOLDERS)
                    .child(folderId)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
                    .addListenerForSingleValueEvent(valueEventListener);

        }

    }

    public Query findBookSearch(String folderId) {
        if (folderId == null) {

            return mDatabaseReference.child(getUserId())
                    .child(REF_FOLDERS)
                    .child(REF_MY_BOOKS_FOLDER)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField");

        } else {

            return mDatabaseReference.child(getUserId())
                    .child(REF_FOLDERS)
                    .child(folderId)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField");

        }
    }

    public void findBook(String folderId, String bookId, ValueEventListener valueEventListener) {

        if (folderId == null)
            folderId = REF_MY_BOOKS_FOLDER;

        mDatabaseReference.child(getUserId()).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(bookId).addListenerForSingleValueEvent(valueEventListener);
    }

    public void fetchFolders(String userId, final ValueEventListener valueEventListener) {

        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addListenerForSingleValueEvent(valueEventListener);

    }

    public Query fetchFolders() {
        return mDatabaseReference.child(getUserId()).child(REF_FOLDERS);
    }


    public void attachFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addChildEventListener(listener);
    }

    public void detachFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.removeEventListener(listener);
    }


    public void deleteFolder(String folderId) {
        mDatabaseReference.child(getUserId()).child(REF_FOLDERS).child(folderId).removeValue();
    }

    public void insertFolder(final Folder folder, final OnPaidOperationListener listener) {

        mDatabaseReference.child(getUserId()).child(REF_FOLDERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference df = mDatabaseReference.child(getUserId()).child(REF_FOLDERS).push();
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

    public void insertDefaulFolder(String description, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference df = mDatabaseReference.child(getUserId()).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER);

        Folder myBooksFolder = new Folder();
        myBooksFolder.setId(UUID.randomUUID().toString());
        myBooksFolder.setDescription(description);
        myBooksFolder.setCustom(false);

        df.setValue(myBooksFolder, completionListener);

    }

    public void updateBook(String folderId, Book bookApi) {

        if (folderId == null)
            folderId = REF_MY_BOOKS_FOLDER;

        mDatabaseReference.child(getUserId()).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).updateChildren(Collections.singletonMap(bookApi.getId(), (Object) bookApi));
    }

    public void updateBookAnnotation(String folderId, String bookId, String annotation) {

        Map<String, Object> mapAnnotation = new HashMap<>();
        mapAnnotation.put("annotation", annotation);

        mDatabaseReference.child(getUserId()).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(bookId).updateChildren(mapAnnotation);
    }

    public void insertBookFolder(String folderId, final Book bookApi, final OnPaidOperationListener listener) {

        final DatabaseReference ref = mDatabaseReference
                .child(getUserId())
                .child(REF_FOLDERS)
                .child(folderId)
                .child("books");


        if (bookApi.getId() == null && bookApi.getIdProvider() != null) { //If it's search book

            ref.orderByChild("idProvider")
                    .equalTo(bookApi.getIdProvider())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() == null) {
                                insert(dataSnapshot, listener, bookApi, ref);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        } else {

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    insert(dataSnapshot, listener, bookApi, ref);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    public void insertBookPopularFolder(final Book book) {

        DatabaseReference ref = mDatabaseReference.child(REF_POPULAR_FOLDER).push();

        book.setId(ref.getKey());

        ref.setValue(book);

    }

    private void insert(DataSnapshot dataSnapshot, OnPaidOperationListener listener, final Book bookApi, DatabaseReference ref) {

        if (bookApi.getId() == null) { //Here we generate our id
            bookApi.setId(ref.push().getKey());
        }

        ref.updateChildren(Collections.singletonMap(bookApi.getId(), (Object) bookApi));
        listener.onInsertBook(true);
    }

    public void deleteBookFolder(String folderId, Book bookApi) {
        mDatabaseReference.child(getUserId()).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(bookApi.getId()).removeValue();
    }

    public void fetchUserById(final ValueEventListener valueEventListener) {

        mDatabaseReference.child(getUserId())
                .addListenerForSingleValueEvent(valueEventListener);

    }

    public interface OnPaidOperationListener {
        void onInsertBook(boolean success); //Return true if insertion was valid

        void onInsertFolder(boolean success);
    }
}
