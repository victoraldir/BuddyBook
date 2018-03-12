package com.quartzodev.inserteditbook;

import com.quartzodev.BasePresenter;
import com.quartzodev.BaseView;
import com.quartzodev.data.Book;

/**
 * Created by victoraldir on 11/03/2018.
 */

public class InsertEditBookContract {

    interface View extends BaseView<Presenter> {
        void expandMoreFields();
        void setLoading(boolean flag);
    }

    interface Presenter extends BasePresenter {
        void loadForm();
        void clickMoreFields();
        void saveBook(Book book);
    }
}
