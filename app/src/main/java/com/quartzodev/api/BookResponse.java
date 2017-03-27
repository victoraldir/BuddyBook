package com.quartzodev.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class BookResponse {

    @SerializedName("kind")
    public String kind;
    @SerializedName("totalItems")
    public int totalItems;
    @SerializedName("items")
    List<Book> items;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<Book> getItems() {
        return items;
    }

    public void setItems(List<Book> items) {
        this.items = items;
    }
}
