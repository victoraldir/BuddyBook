package com.quartzodev.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quartzodev.adapters.ViewPagerAdapter;
import com.quartzodev.buddybook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 31/03/2017.
 */

public class ViewPagerBookFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";

    private String mUserId;
    private ViewPagerAdapter mViewPagerAdapter;
    @BindView(R.id.pager)
    ViewPager mViewPager;

    public static ViewPagerBookFragment newInstance(String userId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_USER_ID, userId);
        ViewPagerBookFragment fragment = new ViewPagerBookFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(ARG_USER_ID)){
            mUserId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadBooksPageView();
    }

    private void loadBooksPageView() {
        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mUserId, getContext());
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

}
