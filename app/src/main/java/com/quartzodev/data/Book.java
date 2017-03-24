package com.quartzodev.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by victoraldir on 23/03/2017.
 */

public class Book {

    @SerializedName("id")
    private String id;

    @SerializedName("kind")
    public String kind;

    @SerializedName("volumeInfo")
    public VolumeInfo volumeInfo;

    class VolumeInfo {

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
    }
}
