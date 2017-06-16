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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (id != null ? !id.equals(book.id) : book.id != null) return false;
        return idProvider != null ? idProvider.equals(book.idProvider) : book.idProvider == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (idProvider != null ? idProvider.hashCode() : 0);
        return result;
    }
}
