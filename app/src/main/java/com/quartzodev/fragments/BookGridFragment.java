package com.quartzodev.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quartzodev.adapters.BookGridAdapter;
import com.quartzodev.api.BookApi;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.Folder;
import com.quartzodev.task.FetchFolderTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class BookGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Folder> {

    private static final String TAG = BookGridFragment.class.getSimpleName();

    private static final String ARG_POSITION_ID = "mFlag";
    private static final String ARG_USER_ID = "mUserId";
    private static final String ARG_FOLDER_ID = "mFolderId";

    public static final int FLAG_MY_BOOKS_FOLDER = 1;
    public static final int FLAG_TOP_BOOKS_FOLDER = 0;
    public static final int FLAG_CUSTOM_FOLDER = 3;

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;
//    @BindView(R.id.grid_book_progress_bar)
//    ProgressBar mProgressBar;

    private BookGridAdapter mAdapter;
    private String mUserId;
    private String mFolderId;
    private int mFlag;
    private OnGridFragmentInteractionListener mListener;

    public static BookGridFragment newInstanceCustomFolder(String userId, String folderId, int flag) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_POSITION_ID, flag);
        arguments.putString(ARG_USER_ID, userId);
        arguments.putString(ARG_FOLDER_ID, folderId);
        BookGridFragment fragment = new BookGridFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public void updateContent(String userId, String folderId, int flag){
        mUserId = userId;
        mFolderId = folderId;
        mFlag = flag;
        getActivity().getSupportLoaderManager().destroyLoader(mFlag);
        getActivity().getSupportLoaderManager().initLoader(mFlag,null,this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(ARG_USER_ID)){
            mUserId = getArguments().getString(ARG_USER_ID);
        }

        if(getArguments().containsKey(ARG_POSITION_ID)){
            mFlag = getArguments().getInt(ARG_POSITION_ID);
        }

        if(getArguments().containsKey(ARG_FOLDER_ID)){
            mFolderId = getArguments().getString(ARG_FOLDER_ID);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(mFlag,null,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grid_book, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new BookGridAdapter(getActivity(),new ArrayList<BookApi>(),mFolderId,mListener);
        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        return rootView;
    }

    @Override
    public Loader<Folder> onCreateLoader(int id, Bundle args) {

        setLoading(true);

        FetchFolderTask task = null;

        //TODO create constants here!
        if(mFlag == FLAG_MY_BOOKS_FOLDER){
            task = new FetchFolderTask(mUserId, null, getActivity(), FetchFolderTask.FETCH_POPULAR_FOLDER);
        }else if(mFlag == FLAG_TOP_BOOKS_FOLDER) {
            task = new FetchFolderTask(mUserId, null, getActivity(), FetchFolderTask.FETCH_MY_BOOKS_FOLDER);
        }else if(mFlag == FLAG_CUSTOM_FOLDER) {
            task = new FetchFolderTask(mUserId, mFolderId, getActivity(), FetchFolderTask.FETCH_CUSTOM_FOLDER);
        }

        task.forceLoad();

        return task;
    }

    public void setLoading(boolean flag){
        ViewGroup container = (ViewGroup)this.getView();

        if (container != null) {
            if(flag) {
                container.findViewById(R.id.grid_book_progress_bar).setVisibility(View.VISIBLE);
                container.findViewById(R.id.recycler_view_books).setVisibility(View.INVISIBLE);
            }else{
                container.findViewById(R.id.grid_book_progress_bar).setVisibility(View.INVISIBLE);
                container.findViewById(R.id.recycler_view_books).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Folder> loader, Folder data) {

        mAdapter.swap(data);
        setLoading(false);

    }

    @Override
    public void onLoaderReset(Loader<Folder> loader) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGridFragmentInteractionListener) {
            mListener = (OnGridFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGridFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnGridFragmentInteractionListener {

        void onClickListenerBookGridInteraction(String mFolderId, BookApi book);

    }
}
