package com.quartzodev.data;

import android.os.Parcel;
import android.os.Parcelable;


import com.google.firebase.database.Exclude;

import java.util.Map;

/**
 * Created by victoraldir on 26/03/2017.
 */

public class Folder  {

    private String description;
    private Map<String,Book> books;

    public Folder(){

    }

    public Folder(String description){
        this.description = description;
    }

    protected Folder(Parcel in) {
        description = in.readString();
    }

//    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
//        @Override
//        public Folder createFromParcel(Parcel in) {
//            return new Folder(in);
//        }
//
//        @Override
//        public Folder[] newArray(int size) {
//            return new Folder[size];
//        }
//    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Book> getBooks() {
        return books;
    }

    public void setBooks(Map<String, Book> books) {
        this.books = books;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel out, int flags) {
//        out.writeString(description);
//    }
}
