package com.quartzodev.data;

/**
 * Created by victoraldir on 14/06/2017.
 */

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by victoraldir on 12/04/2017.
 */

@Keep
public class VolumeInfo {

    public String title;

    public List<String> authors;

    public String publisher;

    public String publishedDate;

    public String description;

    public ImageLink imageLink;

    public String searchField;

    public String isbn10;

    public String isbn13;

    public String pageCount;

    public String language;

    public String printType;

    public String getSearchField() {

        if(title != null) {
            searchField = title;

            if (authors != null && !authors.isEmpty()) {
                for (String author : authors) {
                    searchField = searchField.concat("_" + author);
                }
            }

            searchField = searchField.concat("_" + publisher).concat("_" + isbn10).concat("_" + isbn13);

            return searchField.toLowerCase();
        }

        return "";
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageLink getImageLink() {
        return imageLink;
    }

    public void setImageLink(ImageLink imageLink) {
        this.imageLink = imageLink;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }
}
