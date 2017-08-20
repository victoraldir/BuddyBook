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
    public static final String SEARCH_VIEW_PAGER = "searchViewPager";
    public static final String MAIN_VIEW_PAGER = "mainViewPager";
    public static final String ARG_FOLDER_ID = "argSearchFolderId";
    public static final String ARG_QUERY = "argSearchQuery";
    public static final String ARG_ISBN = "argSearchIsbn";
    public static final String ARG_TYPE_FRAGMENT = "argSearchTypeFragment";

    private String mTypeFragment;
    private String mIsbn;
    private String mQuery;
    private String mFolderId;

    private ViewPagerAdapter mViewPagerAdapter;

    @BindView(R.id.main_pager)
    ViewPager mViewPager;

    public static ViewPagerFragment newInstance(String type, String folderId, String query, String isbn){

        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle arguments = new Bundle();

        arguments.putString(ARG_FOLDER_ID, folderId);
        arguments.putString(ARG_QUERY, query);
        arguments.putString(ARG_ISBN, isbn);
        arguments.putString(ARG_TYPE_FRAGMENT,type);
        viewPagerFragment.setArguments(arguments);

        return viewPagerFragment;
    }

    public ViewPagerFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if(getArguments() != null) {

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


    private List<Fragment> getListFragments(){

        List<Fragment> list = new ArrayList<>();

        if(mTypeFragment.equals(MAIN_VIEW_PAGER)) {
            list.add(BookGridFragment.newInstance(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER,
                    R.menu.menu_my_books));
            list.add(BookGridFragment.newInstance(FirebaseDatabaseHelper.REF_POPULAR_FOLDER,
                    R.menu.menu_search_result));
        }else{

            list.add(SearchResultFragment.newInstance(null,mIsbn)); //Web search
            if(mFolderId == null){
                list.add(SearchResultFragment.newInstance(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER,mIsbn));
            }else{
                list.add(SearchResultFragment.newInstance(mFolderId,mIsbn));
            }

        }

        return list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //When user exits search we show tab with My Book and Explore names
        if(mTypeFragment.equals(SEARCH_VIEW_PAGER) && mFolderId == null){ //mFolderId == null means My Books section
            ((MainActivity) getActivity()).setupViewPager(mViewPager,MAIN_VIEW_PAGER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).setupViewPager(mViewPager,mTypeFragment);

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

    public void executeSearch(String query, Integer maxResult) {
        List<Fragment> fragmentList =  mViewPagerAdapter.getFragmentsList();

        for (int x=0; x<fragmentList.size(); x++){
            Fragment fragment = fragmentList.get(x);

            if(fragment instanceof SearchResultFragment){
                ((SearchResultFragment) fragment).executeSearchSearchFragment(query,maxResult);
            }
        }
    }
}
