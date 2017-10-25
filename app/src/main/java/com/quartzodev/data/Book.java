package com.quartzodev.data;

import android.support.annotation.Keep;

/**
 * Created by victoraldir on 23/03/2017.
 */

@Keep
public class Book {

    private String id;

    private String idProvider;

    private String typeProvider;

    private String kind;

    private String annotation;

    private VolumeInfo volumeInfo;

    private Lend lend;

    private boolean isCustom;

    public Book() {
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public Lend getLend() {
        return lend;
    }

    public void setLend(Lend lend) {
        this.lend = lend;
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

    public String getIdProvider() {
        return idProvider;
    }

    public void setIdProvider(String idProvider) {
        this.idProvider = idProvider;
    }

    public String getTypeProvider() {
        return typeProvider;
    }

    public void setTypeProvider(String typeProvider) {
        this.typeProvider = typeProvider;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;

        Book book = (Book) o;

        if (getId() != null ? !getId().equals(book.getId()) : book.getId() != null) return false;
        if (getIdProvider() != null ? !getIdProvider().equals(book.getIdProvider()) : book.getIdProvider() != null)
            return false;
        return getLend() != null ? getLend().equals(book.getLend()) : book.getLend() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getIdProvider() != null ? getIdProvider().hashCode() : 0);
        result = 31 * result + (getLend() != null ? getLend().hashCode() : 0);
        return result;
    }
}
