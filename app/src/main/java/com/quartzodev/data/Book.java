package com.quartzodev.data;

/**
 * Created by victoraldir on 26/03/2017.
 */

public class Book {

    public String photoUrl;
    public String tittle;
    public String author;
    public String description;

    public static Book newFirebaseBook(com.quartzodev.api.Book book){

        Book newBook = new Book();

        com.quartzodev.api.Book.VolumeInfo volumeInfo = book.volumeInfo;

        newBook.photoUrl = volumeInfo.imageLink.thumbnail;
        newBook.tittle = volumeInfo.title;
        newBook.author = volumeInfo.authors != null ? volumeInfo.authors.get(0) : null;
        newBook.description = volumeInfo.description;

        return newBook;
    }

}
