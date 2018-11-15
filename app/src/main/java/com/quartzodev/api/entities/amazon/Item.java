package com.quartzodev.api.entities.amazon;

import org.simpleframework.xml.Element;

public class Item {

    @Element(name = "DetailPageURL")
    public String detailPageURL;

    public String getDetailPageURL() {
        return detailPageURL;
    }

    public void setDetailPageURL(String detailPageURL) {
        this.detailPageURL = detailPageURL;
    }

}
