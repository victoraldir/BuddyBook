package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Folder;
import com.quartzodev.task.FetchFolderTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class GridBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Folder> {

    private static final int LOADER_ID_LIST_BOOKS = 1;
    private static final String ARG_POSITION_ID = "mFlag";
    private static final String ARG_USER_ID = "mUserId";
    private static final String ARG_FOLDER_ID = "mFolderId";

    public static final int FLAG_MY_BOOKS_FOLDER = 0;
    public static final int FLAG_TOP_BOOKS_FOLDER = 1;
    public static final int FLAG_CUSTOM_FOLDER = 3;


    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private String mUserId;
    private String mFolderId;
    private int mFlag;

    public static GridBookFragment newInstance(String userId, int flag) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_POSITION_ID, flag);
        arguments.putString(ARG_USER_ID, userId);
        GridBookFragment fragment = new GridBookFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public static GridBookFragment newInstance(String userId, String folderId, long flag) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_POSITION_ID, flag);
        arguments.putString(ARG_USER_ID, userId);
        arguments.putString(ARG_FOLDER_ID, folderId);
        GridBookFragment fragment = new GridBookFragment();
        fragment.setArguments(arguments);
        return fragment;
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
        getLoaderManager().initLoader(LOADER_ID_LIST_BOOKS,null,this).forceLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grid_book, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new Adapter(getActivity(),new ArrayList<com.quartzodev.data.Book>());
        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        return rootView;
    }

    @Override
    public Loader<Folder> onCreateLoader(int id, Bundle args) {

        FetchFolderTask task = null;

        //TODO create constants here!
        if(mFlag == FLAG_MY_BOOKS_FOLDER){
            task = new FetchFolderTask(mUserId, null, getActivity(), FetchFolderTask.FETCH_POPULAR_FOLDER);
        }else if(mFlag == FLAG_TOP_BOOKS_FOLDER) {
            task = new FetchFolderTask(mUserId, null, getActivity(), FetchFolderTask.FETCH_MY_BOOKS_FOLDER);
        }else if(mFlag == FLAG_CUSTOM_FOLDER) {
            task = new FetchFolderTask(mUserId, mFolderId, getActivity(), FetchFolderTask.FETCH_CUSTOM_FOLDER);
        }

        return task;
    }

    @Override
    public void onLoadFinished(Loader<Folder> loader, Folder data) {
        mAdapter.swap(data);
    }

    @Override
    public void onLoaderReset(Loader<Folder> loader) {

    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private Context mContext;
        private List<com.quartzodev.data.Book> bookList;

        public Adapter(Context mContext, List<com.quartzodev.data.Book> bookList) {
            this.mContext = mContext;
            this.bookList = bookList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.item_book, parent, false);
            final ViewHolder vh = new ViewHolder(view);

            return vh;
        }

        public void swap(Folder folder){
            if(folder != null && folder.getBooks() != null) {
                this.bookList = new ArrayList<>(folder.getBooks().values());
                notifyDataSetChanged();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            com.quartzodev.data.Book book = bookList.get(position);
            //Book.VolumeInfo bookVolumeInfo = book.volumeInfo;

            holder.textViewBookTitte.setText(book.tittle);
            //if(bookVolumeInfo.authors != null)
            holder.textViewBookAuthor.setText(book.author);

            if(book.photoUrl != null) {
                Glide.with(mContext)
                        .load(book.photoUrl)
                        .centerCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .error(android.R.drawable.ic_dialog_alert)
                        .into(holder.ImageViewthumbnail);
            }

        }

        @Override
        public int getItemCount() {
            return bookList != null ? bookList.size() : 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail) ImageView ImageViewthumbnail;
        @BindView(R.id.book_title) TextView textViewBookTitte;
        @BindView(R.id.book_author) TextView textViewBookAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
