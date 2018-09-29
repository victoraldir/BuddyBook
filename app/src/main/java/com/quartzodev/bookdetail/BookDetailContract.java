package com.quartzodev.bookdetail;

import com.quartzodev.BasePresenter;
import com.quartzodev.BaseView;
import com.quartzodev.inserteditbook.InsertEditBookContract;

/**
 * Created by victoraldir on 18/03/2018.
 */

public class BookDetailContract {

    public interface View extends BaseView<InsertEditBookContract.Presenter> {
        void showBook();
    }

    public interface Presenter extends BasePresenter {

    }

}
