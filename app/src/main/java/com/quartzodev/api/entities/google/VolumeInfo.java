package com.quartzodev.api.entities.google;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.Keep;

/**
 * Created by victoraldir on 12/04/2017.
 */

@Keep
public class VolumeInfo {

    @SerializedName("title")
    public String title;
    @SerializedName("authors")
    public List<String> authors;
    @SerializedName("publisher")
    public String publisher;
    @SerializedName("publishedDate")
    public String publishedDate;
    @SerializedName("description")
    public String description;
    @SerializedName("imageLinks")
    public ImageLink imageLink;
    @SerializedName("searchField")
    public String searchField;
    @SerializedName("pageCount")
    public String pageCount;
    @SerializedName("language")
    public String language;
    @SerializedName("printType")
    public String printType;
    @SerializedName("industryIdentifiers")
    public List<IndustryIdentifier> industryIdentifiers;

    public String getSearchField() {

        searchField = title;

        if (authors != null && !authors.isEmpty()) {
            for (String author : authors) {
                searchField = searchField.concat("_" + author);
            }
        }

        searchField = searchField.concat("_" + publisher);

        return searchField.toLowerCase();
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

    public List<IndustryIdentifier> getIndustryIdentifiers() {
        return industryIdentifiers;
    }

    public void setIndustryIdentifiers(List<IndustryIdentifier> industryIdentifiers) {
        this.industryIdentifiers = industryIdentifiers;
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
