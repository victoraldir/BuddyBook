package com.quartzodev.api.entities.amazon;

import org.simpleframework.xml.Element;

import java.util.List;


public class ItemLookupResponse {

    public List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
