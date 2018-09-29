package com.quartzodev.inserteditbook;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.ImageLink;
import com.quartzodev.data.VolumeInfo;

import java.util.List;

/**
 * Created by victoraldir on 11/03/2018.
 */

public class InsertEditBookPresenter implements InsertEditBookContract.Presenter {

    @NonNull
    private final InsertEditBookContract.View mInsertEditBookView;
    private String mUserId;
    private String mFolderId;
    private String mBookId;
    private String mImagePath;
    private boolean mFlagFieldsOpen;

    InsertEditBookPresenter(InsertEditBookContract.View view, String userId, String folderId,
                            String bookId, String imagePath, boolean isMoreFieldsOpen){

        mInsertEditBookView = view;
        mBookId = bookId;
        mFolderId = folderId;
        mUserId = userId;
        mImagePath = imagePath;
        mFlagFieldsOpen = isMoreFieldsOpen;
        view.setPresenter(this);
    }

    @Override
    public void loadBook() {
        FirebaseDatabaseHelper.getInstance().findBook(mUserId, mFolderId, mBookId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Book book = dataSnapshot.getValue(Book.class);
                    if(book != null && book.getVolumeInfo() != null) {
                        if(book.getVolumeInfo().getImageLink() != null) {
                            mImagePath = book.getVolumeInfo().getImageLink().getSmallThumbnail();
                        }else{
                            mInsertEditBookView.showNoPictureAvailable();
                        }
                        mInsertEditBookView.showBook(book);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void loadForm() {
        if(mBookId == null) {
            mInsertEditBookView.setLoading(false);
        }else{
            mInsertEditBookView.setLoadingPhoto(true);
            loadBook();
        }

        if(mImagePath != null)
            mInsertEditBookView.loadChosenImage(Uri.parse(mImagePath));

        if(mFlagFieldsOpen)
            mInsertEditBookView.expandMoreFields();
    }

    @Override
    public void clickMoreFields() {
        mFlagFieldsOpen = true;
        mInsertEditBookView.expandMoreFields();
    }

    @Override
    public void saveBook(String title, List<String> authors, String isbn13, String isbn10,
                         String language, String pageCount, String printType, String publisher,
                         String description, String annotation, String imagePath) {

        Book newBook = new Book();
        VolumeInfo volumeInfo = new VolumeInfo();
        ImageLink imageLink = new ImageLink();

        newBook.setId(mBookId);

        if(imagePath != null){
            imageLink.setThumbnail(imagePath);
            imageLink.setSmallThumbnail(imagePath);
        }

        volumeInfo.setTitle(title);
        volumeInfo.setAuthors(authors);
        volumeInfo.setIsbn13(isbn13);
        volumeInfo.setIsbn10(isbn10);
        volumeInfo.setLanguage(language);
        volumeInfo.setPageCount(pageCount);
        volumeInfo.setPrintType(printType);
        volumeInfo.setPublisher(publisher);
        volumeInfo.setDescription(description);

        newBook.setAnnotation(annotation);

        volumeInfo.setImageLink(imageLink);
        newBook.setVolumeInfo(volumeInfo);
        newBook.setCustom(true);

        if(validateBook(newBook)){
            if(mFolderId == null)
                mFolderId = FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER;

            FirebaseDatabaseHelper.getInstance().insertBookFolder(mUserId, mFolderId, newBook, new FirebaseDatabaseHelper.OnPaidOperationListener() {
                @Override
                public void onInsertBook(boolean success) {
                    if(success){
                        mInsertEditBookView.finishActivity();
                    }
                }

                @Override
                public void onInsertFolder(boolean success) {

                }
            });
        }

    }

    @Override
    public boolean validateBook(Book book) {
        if(book.getVolumeInfo().getTitle() == null) {
            mInsertEditBookView.setErrorMessage("Title cannot be empty");
            return false;
        }

        if (book.getVolumeInfo().getTitle().isEmpty()){
            mInsertEditBookView.setErrorMessage("Title cannot be empty");
            return false;
        }

        return true;
    }

    @Override
    public void openCamera() {
        mInsertEditBookView.launchCameraActivity();
    }

    @Override
    public void openGallery() {
        mInsertEditBookView.launchGalleryActivity();
    }

    @Override
    public void openCameraGallery() {
        if(!mInsertEditBookView.hasExternalPermission()) {
            mInsertEditBookView.requestCameraPermission();
        }else{
            mInsertEditBookView.showCaptureOptions();
        }
    }

    @Override
    public void setCameraResult(Object image) {
        mInsertEditBookView.setLoadingPhoto(true);
        mInsertEditBookView.loadChosenImage(image);
    }

    @Override
    public void setGalleryResult(Object image) {
        mInsertEditBookView.setLoadingPhoto(true);
        mInsertEditBookView.loadChosenImage(image);
    }

    @Override
    public void start() {}

    public String getUserId() {
        return mUserId;
    }

    public String getFolderId() {
        return mFolderId;
    }

    public String getBookId() {
        return mBookId;
    }

    public boolean getFlagFieldsOpen(){
        return mFlagFieldsOpen;
    }

    public String getImagePath(){
        return mImagePath;
    }
}
