package com.quartzodev.inserteditbook;

import com.quartzodev.BasePresenter;
import com.quartzodev.BaseView;
import com.quartzodev.data.Book;

import java.util.List;

/**
 * Created by victoraldir on 11/03/2018.
 */

public class InsertEditBookContract {

    public interface View extends BaseView<Presenter> {

        void showBook(Book book);

        void showCaptureOptions();

        void showNoPictureAvailable();

        void launchCameraActivity();

        void launchGalleryActivity();

        void loadChosenImage(Object image);

        void expandMoreFields();

        void setLoading(boolean flag);

        void setLoadingPhoto(boolean flag);

        void setErrorMessage(String msg);

        void requestCameraPermission();

        boolean hasExternalPermission();

        void finishActivity();

    }

    public interface Presenter extends BasePresenter {

        void loadBook();

        void loadForm();

        void clickMoreFields();

        void saveBook(String title, List<String> authors, String isbn13, String isbn10,
                      String language, String pageCount, String printType, String publisher,
                      String description, String annotation, String imagePath);

        boolean validateBook(Book book);

        void openCamera();

        void openGallery();

        void openCameraGallery();

        void setCameraResult(Object image);

        void setGalleryResult(Object image);
    }
}
