package com.quartzodev.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.quartzodev.buddybook.R;
import com.quartzodev.utils.DateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by victoraldir on 25/03/2017.
 */

public class User implements Parcelable {

    private String uid;
    private String email;
    private String username;
    private String photoUrl;
    private String lastActivity;
    private Map<String, Folder> folders;

    public User() {

    }

    public User(String uid, String email, String username, String photoUrl, String lastActivity) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.photoUrl = photoUrl;
        this.lastActivity = lastActivity;
    }

    public static User setupUserFirstTime(FirebaseUser firebaseUser, Context context){

        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl() == null ? null : firebaseUser.getPhotoUrl().toString(),
                DateUtils.getCurrentTimeString());

        try {
            if (user.getPhotoUrl() == null) {
                for (UserInfo userInfo : firebaseUser.getProviderData()) {
                    if (userInfo.getPhotoUrl() != null) {
                        user.setPhotoUrl(userInfo.getPhotoUrl().toString());
                        break;
                    }
                }
            }
        }catch (Exception ex){

        }

        Folder myBooksFolder = new Folder(context.getResources().getString(R.string.tab_my_books));
        myBooksFolder.setId(UUID.randomUUID().toString());
        user.setFolders(Collections.singletonMap(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER,myBooksFolder));
//
//        myBooksFolder.setBooks(generateFakeBooksMap(3));
//
//
//        //user.setMyBooksFolder(myBooksFolder);
//
//        user.setFolders(generateFakeFolderMap(10));


        return  user;

    }

//    private static Map<String, Folder> generateFakeFolderMap(int rows){
//
//        Map<String, Folder> folderList = new HashMap<>();
//
//        for(int x = 0; x < rows; x++){
//
//            Folder folder = new Folder("Custom folder " + x);
//
//            Book book = new Book();
//            book.photoUrl = "http://www.gweissert.com/wp-content/uploads/self-healing-and-self-care-books.jpg";
//            book.description = "book test " + x;
//            book.title = "Tittle " + x;
//            book.authors = "Author test " + x;
//
//            folder.setBooks(Collections.singletonMap(book.description,book));
//            folder.setId(UUID.randomUUID().toString());
//
//            folderList.put(folder.getId(),folder);
//        }
//
//        return folderList;
//    }
//
//    private static Map<String, Book> generateFakeBooksMap(int rows){
//
//        Map<String, Book> bookHashMap = new HashMap<>();
//
//
//        for(int x = 0; x < rows; x++){
//            Book book = new Book();
//            book.photoUrl = "http://www.gweissert.com/wp-content/uploads/self-healing-and-self-care-books.jpg";
//            book.description = "book test " + x;
//            book.title = "Tittle " + x;
//            book.authors = "Author test " + x;
//
//            bookHashMap.put(book.description,book);
//        }
//
//        return bookHashMap;
//    }

    public Map<String, Folder> getFolders() {
        return folders;
    }

    public void setFolders(Map<String, Folder> folders) {
        this.folders = folders;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uid);
        out.writeString(email);
        out.writeString(username);
        out.writeString(photoUrl);
        out.writeString(lastActivity);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        username = in.readString();
        photoUrl = in.readString();
        lastActivity = in.readString();


    }
}
