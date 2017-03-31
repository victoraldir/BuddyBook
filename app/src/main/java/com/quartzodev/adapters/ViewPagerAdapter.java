package com.quartzodev.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.fragments.GridBookFragment;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private String mUserId;

    public ViewPagerAdapter(FragmentManager fm, String userId, Context context) {
        super(fm);

        mContext = context;
        this.mUserId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        return GridBookFragment.newInstance(mUserId, position);
    }
    @Override
    public int getCount() {
        return MainActivity.NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position == 1){
            return mContext.getResources().getString(R.string.tab_my_books);
        }else{
            return mContext.getResources().getString(R.string.tab_top_books);
        }

    }

}

