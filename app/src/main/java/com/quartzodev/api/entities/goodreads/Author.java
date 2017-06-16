package com.quartzodev.api.entities.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by victoraldir on 15/06/2017.
 */

@Root(name = "author",strict = false)
public class Author {

    @Element
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
