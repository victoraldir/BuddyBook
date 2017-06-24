package com.quartzodev.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.fragments.BookGridFragment;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_PAGES = 2;

    private String mUserId;

    public ViewPagerAdapter(FragmentManager fm, String userId) {
        super(fm);

        this.mUserId = userId;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        try {
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException) {
            System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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

//    @Override
//    public CharSequence getPageTitle(int position) {
//
//        if (position == 1) {
//            return mContext.getResources().getString(R.string.tab_top_books);
//        } else {
//            return mContext.getResources().getString(R.string.tab_my_books);
//        }
//
//    }

}

