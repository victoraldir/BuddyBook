package com.quartzodev.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quartzodev.adapters.ViewPagerAdapter;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 31/03/2017.
 */

public class ViewPagerFragment extends Fragment {

    private static final String TAG = ViewPagerFragment.class.getSimpleName();

    @BindView(R.id.main_pager)
    ViewPager mViewPager;

    public ViewPagerFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ButterKnife.bind(this, rootView);

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getListFragments());
        mViewPager.setAdapter(mViewPagerAdapter);

        return rootView;
    }


    private List<BookGridFragment> getListFragments(){

        List<BookGridFragment> list = new ArrayList<>();
        list.add(BookGridFragment.newInstance(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER,
                R.menu.menu_my_books));
        list.add(BookGridFragment.newInstance(FirebaseDatabaseHelper.REF_POPULAR_FOLDER,
                R.menu.menu_search_result));

        return list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setupViewPager(mViewPager);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).hideTab();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
