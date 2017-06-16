package com.quartzodev.api.entities.google;

import com.google.gson.annotations.SerializedName;

/**
 * Created by victoraldir on 12/04/2017.
 */

public class ImageLink {

    @SerializedName("smallThumbnail")
    public String smallThumbnail;

    @SerializedName("thumbnail")
    public String thumbnail;

    public ImageLink() {
    }

    public String getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(String smallThumbnail) {
        this.smallThumbnail = smallThumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
