package com.quartzodev.api.entities.google;

import com.google.gson.annotations.SerializedName;

/**
 * Created by victoraldir on 22/06/2017.
 */

public class IndustryIdentifier {

    @SerializedName("type")
    private String type;
    @SerializedName("identifier")
    private String identifier;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
