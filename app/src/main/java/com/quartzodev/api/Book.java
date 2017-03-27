package com.quartzodev.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by victoraldir on 23/03/2017.
 */

public class Book implements Parcelable {

    @SerializedName("id")
    public String id;

    @SerializedName("kind")
    public String kind;

    @SerializedName("volumeInfo")
    public VolumeInfo volumeInfo;

    public Book(){

    }

    protected Book(Parcel in) {
        id = in.readString();
        kind = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(kind);
    }

    public static class VolumeInfo {

        @SerializedName("title")
        public String title;

        @SerializedName("authors")
        public List<String> authors;

        @SerializedName("publisher")
        public String publisher;

        @SerializedName("publishedDate")
        public String publishedDate;

        @SerializedName("description")
        public String description;

        @SerializedName("imageLinks")
        public ImageLink imageLink;

        public class ImageLink{

            @SerializedName("smallThumbnail")
            public String smallThumbnail;

            @SerializedName("thumbnail")
            public String thumbnail;
        }
    }
}
