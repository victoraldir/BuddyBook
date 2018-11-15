package com.quartzodev.api.strategies;

import com.quartzodev.api.entities.amazon.ItemLookupResponse;
import com.quartzodev.api.interfaces.BaseAPI;
import com.quartzodev.api.interfaces.IAmazonAPI;
import com.quartzodev.api.interfaces.IQuery;
import com.quartzodev.data.Book;
import com.quartzodev.data.VolumeInfo;
import com.quartzodev.utils.Signatures;

import java.io.IOException;
import java.util.List;

public class AmazonImpl implements IQuery{

    private IAmazonAPI iAmazonAPI;

    public AmazonImpl(IAmazonAPI amazonAPI) {
        iAmazonAPI = amazonAPI;
    }

    @Override
    public List<Book> getBooks(String query) {
        return null;
    }

    @Override
    public List<Book> getBooksMaxResult(String query, int maxResults) {
        return null;
    }

    @Override
    public Book getBookByISBN(String isbn) {

        String secretKey = "randomSecret";
        String requestUrl = "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&Operation=ItemLookup&ResponseGroup=Large&SearchIndex=All&IdType=ISBN&ItemId={{ISBN}}&AWSAccessKeyId=bla&AssociateTag=victoraldir-20";

        requestUrl = requestUrl.replace("{{ISBN}}",isbn);

        String signedUrl  = Signatures.sign(requestUrl, "HmacSHA256", secretKey);

        Book book = new Book();

        ItemLookupResponse itemLookupResponse = null;
        try {
            itemLookupResponse = iAmazonAPI.getBook(signedUrl).execute().body();

            VolumeInfo volumeInfo = new VolumeInfo();

            volumeInfo.setDescription(itemLookupResponse.getItems().get(0).getDetailPageURL());

            book.setVolumeInfo(volumeInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return book;
    }
}
