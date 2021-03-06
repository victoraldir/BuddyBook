package com.quartzodev.adapters;

import android.os.Parcelable;
import android.view.ViewGroup;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mBookGridFragments;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> bookGridFragments) {
        super(fm);
        this.mBookGridFragments = bookGridFragments;
    }

    public List<Fragment> getFragmentsList() {
        return mBookGridFragments;
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
        return mBookGridFragments.indexOf(object);
    }

    @Override
    public Fragment getItem(int position) {
        return mBookGridFragments.get(position);
    }

    @Override
    public int getCount() {
        return mBookGridFragments.size();
    }


}

