package com.quartzodev.inserteditbook;

import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.ImageLink;
import com.quartzodev.data.VolumeInfo;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by victoraldir on 11/03/2018.
 */

public class InsertEditBookPresenter implements InsertEditBookContract.Presenter {

    @NonNull
    private final InsertEditBookContract.View mInsertEditBookView;
    private String mFolderId;
    private String mBookId;
    private String mImagePath;
    private boolean mFlagFieldsOpen;
    private Book mEditedBook;

    InsertEditBookPresenter(InsertEditBookContract.View view, String userId, String folderId,
                            String bookId, String imagePath, boolean isMoreFieldsOpen) {

        mInsertEditBookView = view;
        mBookId = bookId;
        mFolderId = folderId;
        mImagePath = imagePath;
        mFlagFieldsOpen = isMoreFieldsOpen;
        view.setPresenter(this);
    }

    @Override
    public void loadBook() {
        FirebaseDatabaseHelper.getInstance().findBook(mFolderId, mBookId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mEditedBook = dataSnapshot.getValue(Book.class);
                    if (mEditedBook != null && mEditedBook.getVolumeInfo() != null) {
                        if (mEditedBook.getVolumeInfo().getImageLink() != null) {
                            if(mEditedBook.getVolumeInfo().getImageLink().getSmallThumbnail() != null){
                                mImagePath = mEditedBook.getVolumeInfo().getImageLink().getSmallThumbnail();
                            }else if(mEditedBook.getVolumeInfo().getImageLink().getThumbnail() != null){
                                mImagePath = mEditedBook.getVolumeInfo().getImageLink().getThumbnail();
                            }else {
                                mInsertEditBookView.showNoPictureAvailable();
                            }

                        } else {
                            mInsertEditBookView.showNoPictureAvailable();
                        }
                        mInsertEditBookView.showBook(mEditedBook);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void loadForm() {
        if (mBookId == null) {
            mInsertEditBookView.setLoading(false);
        } else {
            mInsertEditBookView.setLoadingPhoto(true);
            loadBook();
        }

        if (mImagePath != null)
            mInsertEditBookView.loadChosenImage(Uri.parse(mImagePath));

        if (mFlagFieldsOpen)
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

        if (imagePath != null) {
            imageLink.setThumbnail(imagePath);
            imageLink.setSmallThumbnail(imagePath);
        }else{
            imageLink.setThumbnail(mImagePath);
            imageLink.setSmallThumbnail(mImagePath);
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

        if (validateBook(newBook)) {
            if (mFolderId == null)
                mFolderId = FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER;

            FirebaseDatabaseHelper.getInstance().insertBookFolder(mFolderId, newBook, new FirebaseDatabaseHelper.OnPaidOperationListener() {
                @Override
                public void onInsertBook(boolean success) {
                    if (success) {
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
    public void updateBook(String title, List<String> authors, String isbn13, String isbn10, String language, String pageCount, String printType, String publisher, String description, String annotation, String imagePath) {
        if(mBookId != null){

            Book newBook = mEditedBook;
            VolumeInfo volumeInfo = mEditedBook.getVolumeInfo();
            ImageLink imageLink = mEditedBook.getVolumeInfo().getImageLink();
            if(imageLink == null)
                imageLink = new ImageLink();

            newBook.setId(mBookId);

            if (imagePath != null) {
                imageLink.setThumbnail(imagePath);
                imageLink.setSmallThumbnail(imagePath);
            }else{
                imageLink.setThumbnail(mImagePath);
                imageLink.setSmallThumbnail(mImagePath);
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

            if (validateBook(newBook)) {
                if (mFolderId == null)
                    mFolderId = FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER;

                FirebaseDatabaseHelper.getInstance().insertBookFolder(mFolderId, newBook, new FirebaseDatabaseHelper.OnPaidOperationListener() {
                    @Override
                    public void onInsertBook(boolean success) {
                        if (success) {
                            mInsertEditBookView.finishActivity();
                        }
                    }

                    @Override
                    public void onInsertFolder(boolean success) {

                    }
                });
            }

        }
    }

    @Override
    public boolean validateBook(Book book) {
        if (book.getVolumeInfo().getTitle() == null) {
            mInsertEditBookView.setErrorMessage("Title cannot be empty");
            return false;
        }

        if (book.getVolumeInfo().getTitle().isEmpty()) {
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
        if (!mInsertEditBookView.hasExternalPermission()) {
            mInsertEditBookView.requestCameraPermission();
        } else {
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
    public void start() {
    }

    public String getFolderId() {
        return mFolderId;
    }

    public String getBookId() {
        return mBookId;
    }

    public boolean getFlagFieldsOpen() {
        return mFlagFieldsOpen;
    }

    public String getImagePath() {
        return mImagePath;
    }
}
