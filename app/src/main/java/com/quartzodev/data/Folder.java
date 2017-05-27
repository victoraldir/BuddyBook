package com.quartzodev.data;

import android.os.Parcel;
import android.support.annotation.Keep;

import com.quartzodev.api.BookApi;

import java.util.Map;

/**
 * Created by victoraldir on 26/03/2017.
 */
@Keep
public class Folder {

    public Folder() {
    }

    private String id;
    private String description;
    private boolean isCustom;
    private Map<String, BookApi> books;

    public Folder(String description) {
        this.description = description;
    }

    protected Folder(Parcel in) {
        description = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, BookApi> getBooks() {
        return books;
    }

    public void setBooks(Map<String, BookApi> books) {
        this.books = books;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Folder folder = (Folder) o;

        return description.equals(folder.description);

    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
