package com.quartzodev.api.entities.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by victoraldir on 14/06/2017.
 */

@Root(strict = false)
public class Book {

    @Element
    private String id;

    @Element
    private String title;

    @Element(name ="image_url")
    private String imageUrl;

    @Element(name ="small_image_url")
    private String smallImageUrl;

    @Element(name ="isbn", required = false)
    private String isbn;

    @Element(name ="isbn13", required = false)
    private String isbn13;

    @Element(name ="description", required = false)
    private String description;

    @Element(name ="publisher", required = false)
    private String publisher;

    @ElementList(name ="authors", required = false)
    private List<Author> authors;

    @Element(name = "publication_day",required = false)
    private Integer day;

    @Element(name = "publication_month", required = false)
    private Integer month;

    @Element(name = "publication_year", required = false)
    private Integer year;

    @Element(name = "num_pages", required = false)
    private String numPages;

    @Element(name = "format", required = false)
    private String format;

    @Element(name = "language_code", required = false)
    private String languageCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;

    }

    public String getNumPages() {
        return numPages;
    }

    public void setNumPages(String numPages) {
        this.numPages = numPages;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getPublishDate(){
        if(year != null && year != null && day != null) {
            return year + "-" + month + "-" + day;
        }

        return null;
    }

    public void setYear(Integer year) {
        this.year = year;
    }


}
