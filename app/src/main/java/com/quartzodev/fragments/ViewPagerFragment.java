package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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

    public static final String SEARCH_VIEW_PAGER = "searchViewPager";
    public static final String MAIN_VIEW_PAGER = "mainViewPager";
    public static final String ARG_FOLDER_ID = "argSearchFolderId";
    public static final String ARG_QUERY = "argSearchQuery";
    public static final String ARG_ISBN = "argSearchIsbn";
    public static final String ARG_TYPE_FRAGMENT = "argSearchTypeFragment";

    private String mTypeFragment;
    private String mIsbn;
    private String mQuery;
    private Integer mMaxResult;
    private String mFolderId;

    private ViewPagerAdapter mViewPagerAdapter;

    @BindView(R.id.main_pager)
    ViewPager mViewPager;

    public static ViewPagerFragment newInstance(String type, String folderId, String query, String isbn) {

        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle arguments = new Bundle();

        arguments.putString(ARG_FOLDER_ID, folderId);
        arguments.putString(ARG_QUERY, query);
        arguments.putString(ARG_ISBN, isbn);
        arguments.putString(ARG_TYPE_FRAGMENT, type);
        viewPagerFragment.setArguments(arguments);

        return viewPagerFragment;
    }

    public ViewPagerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {

            if (getArguments().containsKey(ARG_FOLDER_ID)) {
                mFolderId = getArguments().getString(ARG_FOLDER_ID);
            }

            if (getArguments().containsKey(ARG_QUERY)) {
                mQuery = getArguments().getString(ARG_QUERY);
            }

            if (getArguments().containsKey(ARG_ISBN)) {
                mIsbn = getArguments().getString(ARG_ISBN);
            }

            if (getArguments().containsKey(ARG_TYPE_FRAGMENT)) {
                mTypeFragment = getArguments().getString(ARG_TYPE_FRAGMENT);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ButterKnife.bind(this, rootView);

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getListFragments());
        mViewPager.setAdapter(mViewPagerAdapter);

        return rootView;
    }


    private List<Fragment> getListFragments() {

        List<Fragment> list = new ArrayList<>();

        if (mTypeFragment.equals(MAIN_VIEW_PAGER)) {
            list.add(BookGridFragment.newInstance(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER,
                    R.menu.menu_my_books));
            list.add(BookGridFragment.newInstance(FirebaseDatabaseHelper.REF_POPULAR_FOLDER,
                    R.menu.menu_search_result));
        } else {

            list.add(SearchResultFragment.newInstance(null, mIsbn, R.menu.menu_search_result)); //Web search
            if (mFolderId == null) {
                list.add(SearchResultFragment.newInstance(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER, mIsbn, R.menu.menu_my_books));
            } else {
                list.add(SearchResultFragment.newInstance(mFolderId, mIsbn, R.menu.menu_my_books));
            }

        }

        return list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public String getTypeFragment() {
        return mTypeFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = ((MainActivity) getActivity());

        activity.getTabLayout().setupWithViewPager(mViewPager);
        activity.checkTabLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void executeSearch(String query, Integer maxResult) {
        mQuery = query;
        mMaxResult = maxResult;
        List<Fragment> fragmentList = mViewPagerAdapter.getFragmentsList();

        for (int x = 0; x < fragmentList.size(); x++) {
            Fragment fragment = fragmentList.get(x);

            if (fragment instanceof BookGridFragment) {
                if (((BookGridFragment) fragment).getFolderId() != null &&
                        !((BookGridFragment) fragment).getFolderId().equals(FirebaseDatabaseHelper.REF_POPULAR_FOLDER))
                    ((BookGridFragment) fragment).refresh();
            } else if (fragment instanceof SearchResultFragment) {
                ((SearchResultFragment) fragment).executeSearchSearchFragment(query, maxResult);
            }
        }
    }

    public void refresh() {
        executeSearch(mQuery, mMaxResult);
    }
}
