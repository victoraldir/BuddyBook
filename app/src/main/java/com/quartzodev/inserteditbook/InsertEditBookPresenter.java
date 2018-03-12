package com.quartzodev.inserteditbook;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.quartzodev.data.Book;

/**
 * Created by victoraldir on 11/03/2018.
 */

public class InsertEditBookPresenter extends AndroidViewModel implements InsertEditBookContract.Presenter {

    @NonNull
    private final InsertEditBookContract.View mInsertEditBookView;

    private String mBookId;

    public InsertEditBookPresenter(@NonNull Application application, InsertEditBookContract.View view, String bookId){
        super(application);

        mInsertEditBookView = view;
        mBookId = bookId;

        view.setPresenter(this);
    }

    @Override
    public void loadForm() {
        if(mBookId == null){
            mInsertEditBookView.setLoading(false);
        }
    }

    @Override
    public void clickMoreFields() {
        mInsertEditBookView.expandMoreFields();
    }

    @Override
    public void saveBook(Book book) {

    }

    @Override
    public void start() {

    }
}
