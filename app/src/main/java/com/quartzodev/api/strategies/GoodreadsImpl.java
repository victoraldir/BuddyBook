package com.quartzodev.api.strategies;

import com.quartzodev.api.entities.goodreads.GoodreadsResponse;
import com.quartzodev.api.interfaces.IGoodreadsAPI;
import com.quartzodev.api.interfaces.IQuery;
import com.quartzodev.buddybook.BuildConfig;
import com.quartzodev.data.Book;
import com.quartzodev.data.ImageLink;
import com.quartzodev.data.VolumeInfo;
import com.quartzodev.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by victoraldir on 15/06/2017.
 */

public class GoodreadsImpl implements IQuery {

    private IGoodreadsAPI mGoodreadsAPI;
    private String mKey;

    public GoodreadsImpl(IGoodreadsAPI igoodreadsAPI) {

        mGoodreadsAPI = igoodreadsAPI;
        mKey = BuildConfig.GOODREADS_API_KEY;
    }

    @Override
    public List<Book> getBooks(String query) {
        return null;
    }

    @Override
    public List<Book> getBooksMaxResult(String isbn, int maxResults) {

        return null;
    }

    @Override
    public Book getBookByISBN(String isbn) {

        Book book = null;

        try {
            GoodreadsResponse goodreadsResponse = mGoodreadsAPI.findBookByISBN(isbn,mKey).execute().body();

            if(goodreadsResponse != null) {
                com.quartzodev.api.entities.goodreads.Book bookApi = goodreadsResponse.getBook();
                book = parseBookApiToBook(bookApi);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return book;
    }

    private Book parseBookApiToBook(com.quartzodev.api.entities.goodreads.Book bookApi){

        Book book = new Book();

        book.setIdProvider(bookApi.getId());

        VolumeInfo volumeInfo = new VolumeInfo();

        volumeInfo.setDescription(bookApi.getDescription());

        volumeInfo.setLanguage(bookApi.getLanguageCode());

        volumeInfo.setPrintType(bookApi.getFormat());

        volumeInfo.setPageCount(bookApi.getNumPages());

        if(bookApi.getPublishDate() != null)
            volumeInfo.setPublishedDate(bookApi.getPublishDate());

        volumeInfo.setPublisher(bookApi.getPublisher());

        if(bookApi.getAuthors() != null && !bookApi.getAuthors().isEmpty()){
            List<String> authors = new ArrayList<>();

            for(int x=0; x<bookApi.getAuthors().size(); x++){
                authors.add(bookApi.getAuthors().get(x).getName());
            }

            volumeInfo.setAuthors(authors);
        }

        if(bookApi.getIsbn() != null){
            volumeInfo.setIsbn10(bookApi.getIsbn());
        }
        if(bookApi.getIsbn13() != null){
            volumeInfo.setIsbn13(bookApi.getIsbn13());
        }

        volumeInfo.setTitle(bookApi.getTitle());

        ImageLink imageLink = new ImageLink();

        imageLink.setThumbnail(bookApi.getImageUrl());
        imageLink.setSmallThumbnail(bookApi.getSmallImageUrl());

        volumeInfo.setImageLink(imageLink);

        book.setVolumeInfo(volumeInfo);

        book.setTypeProvider(Constants.TYPE_PROVIDER_GOODREADS);

        return book;

    }
}
