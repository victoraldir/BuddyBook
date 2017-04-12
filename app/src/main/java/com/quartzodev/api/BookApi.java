package com.quartzodev.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by victoraldir on 23/03/2017.
 */

public class BookApi {

    @SerializedName("id")
    public String id;

    @SerializedName("kind")
    public String kind;

    @SerializedName("volumeInfo")
    public VolumeInfo volumeInfo;

    public BookApi(){
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
}
