package com.quartzodev.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.Folder;
import com.quartzodev.fragments.FolderFragment.OnListFragmentInteractionListener;
import com.quartzodev.fragments.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFolderRecyclerViewAdapter extends RecyclerView.Adapter<MyFolderRecyclerViewAdapter.ViewHolder> {

    private static final int VIEW_TYPE_MY_BOOKS = 0;
    private static final int VIEW_TYPE_FOLDER = 1;
//    private static final int VIEW_TYPE_ADD_FOLDER = 3;

    //private final List<DummyItem> mValues;
    private final List<Folder> mFolderList;
    private final OnListFragmentInteractionListener mListener;

    // Flag to determine if we want to use a separate view for "My Books"
    private boolean mMyBooksLayout = true;

    public MyFolderRecyclerViewAdapter(List<Folder> folderList, OnListFragmentInteractionListener listener) {
        mFolderList = folderList;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        if(position  == 0){
            return VIEW_TYPE_MY_BOOKS ;
        }
        return VIEW_TYPE_FOLDER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        if(viewType == VIEW_TYPE_MY_BOOKS){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_folder_my_books, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_folder, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if(position > 1) {
            holder.mItem = mFolderList.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mFolderList.get(position).getDescription());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFolderList.size() + 1; //My Book well be aways as first
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public Folder mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
