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
import java.util.List;
import java.util.Map;

/**
 * Created by victoraldir on 29/03/2017.
 */

public class FirebaseDatabaseHelper {

    private static final String TAG = FetchFolderTask.class.getSimpleName();

    private static final String ROOT = "users";
    public static final String REF_POPULAR_FOLDER = "_popularBooks"; //See a better way to maintain popular folder
    public static final String REF_MY_BOOKS_FOLDER = "myBooksFolder";
    private static final String REF_FOLDERS = "folders";

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

    public void insertFoldersBooks(String userId, Folder folder){
        DatabaseReference df = mDatabaseReference.child(userId).push();

        String id = df.getKey();

//        df.setValue(book.id,book);

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

    public void insertPopularBooks(Map<String, BookApi> books){

        Folder folder = new Folder("Popular Books Folder");

        folder.setBooks(books);

        mDatabaseReference.child(REF_POPULAR_FOLDER).setValue(folder);

    }

    public void fetchMyBooksFolder(String userId, final OnDataSnapshotListener onDataSnapshotListener){

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void fetchBooksFromFolder(String userId, String folderId, final OnDataSnapshotListener onDataSnapshotListener){

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void findBook(String userId ,String folderId, String bookId, final OnDataSnapshotListener onDataSnapshotListener){

        if(folderId.equals(REF_POPULAR_FOLDER)){

            mDatabaseReference.child(REF_POPULAR_FOLDER).child("books").child(bookId)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        }else{

            mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child("books").child(bookId)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        }



    }

    public void fetchFolders(String userId, final OnDataSnapshotListener onDataSnapshotListener, ChildEventListener listener){

        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        //ref.addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));
        ref.addChildEventListener(listener);

        ref.orderByChild("custom")
                .equalTo(true)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void deleteFolder(String userId, String folderId){
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).removeValue();
    }

    public void insertFolder(String userId, Folder folder){
        DatabaseReference df = mDatabaseReference.child(userId).child(REF_FOLDERS).push();

        folder.setId(df.getKey());

        folder.setCustom(true);

        df.setValue(folder);
    }

    public void insertBookFolder(String userId, String folderId, BookApi bookApi){
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child("books").updateChildren(Collections.singletonMap(bookApi.getId(),(Object) bookApi));
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