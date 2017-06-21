package com.quartzodev.api.strategies;

import com.quartzodev.api.entities.google.BookApi;
import com.quartzodev.api.entities.google.BookResponse;
import com.quartzodev.api.interfaces.IGoogleBookAPI;
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

public class GoogleImpl implements IQuery{

    private IGoogleBookAPI mIGoogleBookAPI;
    private String mKey;

    public GoogleImpl(IGoogleBookAPI iGoogleBookAPI) {
        this.mIGoogleBookAPI = iGoogleBookAPI;
        mKey = BuildConfig.GOOGLE_BOOK_API_KEY;
    }

    @Override
    public List<Book> getBooks(String query) {

        List<Book> bookList = new ArrayList<>();

        try {
            BookResponse bookResponse = mIGoogleBookAPI.getBooks(query, mKey).execute().body();

            if(bookResponse != null && bookResponse.getItems() != null){

                for (int x=0 ; x < bookResponse.getItems().size(); x++){

                    BookApi bookApi = bookResponse.getItems().get(x);
                    bookList.add(parseBookApiToBook(bookApi));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    @Override
    public List<Book> getBooksMaxResult(String query, int maxResults) {

        List<Book> bookList = new ArrayList<>();

        try {
            BookResponse bookResponse = mIGoogleBookAPI.getBooksMaxResult(query,maxResults,mKey).execute().body();

            if(bookResponse != null && bookResponse.getItems() != null){
                for (int x=0 ; x < bookResponse.getItems().size(); x++){

                    BookApi bookApi = bookResponse.getItems().get(x);
                    bookList.add(parseBookApiToBook(bookApi));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    @Override
    public Book getBookByISBN(String isbn) {
        Book book = null;

        try {

            String query = "isbn:" + isbn;

            BookResponse bookResponse = mIGoogleBookAPI.getBooksMaxResult(query,1,mKey).execute().body();
            if(bookResponse != null && bookResponse.getItems() != null && !bookResponse.getItems().isEmpty()){

                    BookApi bookApi = bookResponse.getItems().get(0);
                    book = parseBookApiToBook(bookApi);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return book;
    }

    private Book parseBookApiToBook(BookApi bookApi){

        Book book = null;

        try {

            book = new Book();

            //book.setId(bookApi.getId()); //TODO remove and use Firebase hash!
            book.setIdProvider(bookApi.getId());
            book.setTypeProvider(Constants.TYPE_PROVIDER_GOOGLE);
            book.setCustom(false);
            book.setKind(bookApi.getKind());

            VolumeInfo volumeInfo = new VolumeInfo();

            volumeInfo.setAuthors(bookApi.getVolumeInfo().getAuthors());
            volumeInfo.setTitle(bookApi.getVolumeInfo().getTitle());
            volumeInfo.setPublisher(bookApi.getVolumeInfo().getPublisher());
            volumeInfo.setPublishedDate(bookApi.getVolumeInfo().getPublishedDate());
            volumeInfo.setDescription(bookApi.getVolumeInfo().getDescription());

            if (bookApi.getVolumeInfo() != null && bookApi.getVolumeInfo().getImageLink() != null) {
                ImageLink imageLink = new ImageLink();
                imageLink.setSmallThumbnail(bookApi.getVolumeInfo().getImageLink().getSmallThumbnail());
                imageLink.setThumbnail(bookApi.getVolumeInfo().getImageLink().getThumbnail());
                volumeInfo.setImageLink(imageLink);
            }

            book.setVolumeInfo(volumeInfo);

        }catch (Exception ex){

        }

        return book;
    }
}
