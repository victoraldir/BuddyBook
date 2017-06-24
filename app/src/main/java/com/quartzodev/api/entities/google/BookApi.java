package com.quartzodev.api.entities.google;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by victoraldir on 23/03/2017.
 */

@Keep
public class BookApi {

    @SerializedName("id")
    private String id;

    @SerializedName("kind")
    private String kind;

    @SerializedName("volumeInfo")
    private VolumeInfo volumeInfo;

    public BookApi() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    public void setVolumeInfo(VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookApi bookApi = (BookApi) o;

        return id != null ? id.equals(bookApi.id) : bookApi.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
