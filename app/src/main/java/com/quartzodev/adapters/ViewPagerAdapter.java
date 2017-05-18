package com.quartzodev.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.fragments.BookGridFragment;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_PAGES = 2;

    private Context mContext;
    private String mUserId;

    public ViewPagerAdapter(FragmentManager fm, String userId, Context context) {
        super(fm);

        mContext = context;
        this.mUserId = userId;
    }

    @Override
    public Fragment getItem(int position) {

        String folderId = position == 1 ? FirebaseDatabaseHelper.REF_POPULAR_FOLDER : FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER;

        return BookGridFragment.newInstanceCustomFolder(mUserId, folderId, null, position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 1) {
            return mContext.getResources().getString(R.string.tab_top_books);
        } else {
            return mContext.getResources().getString(R.string.tab_my_books);
        }

    }

}

