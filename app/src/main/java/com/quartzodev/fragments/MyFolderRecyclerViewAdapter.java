package com.quartzodev.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.Folder;
import com.quartzodev.fragments.FolderFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Folder} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyFolderRecyclerViewAdapter extends RecyclerView.Adapter<MyFolderRecyclerViewAdapter.ViewHolder> {

    private static final int ADITIONAL_VIEWS = 2;

    private static final int VIEW_TYPE_MY_BOOKS = 0;
    private static final int VIEW_TYPE_FOLDER = 1;
    private static final int VIEW_TYPE_ADD_FOLDER = 3;

    //private final List<DummyItem> mValues;
    private List<Folder> mFolderList;
    private final OnListFragmentInteractionListener mListener;

    public MyFolderRecyclerViewAdapter(List<Folder> folderList, OnListFragmentInteractionListener listener) {
        mFolderList = folderList;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        if(position  == 0){
            return VIEW_TYPE_MY_BOOKS ;
        }else if (position == ((mFolderList.size() - 1) + ADITIONAL_VIEWS)){
            return VIEW_TYPE_ADD_FOLDER;
        }
        return VIEW_TYPE_FOLDER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if(viewType == VIEW_TYPE_MY_BOOKS){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_books, parent, false);
        }else if( viewType == VIEW_TYPE_ADD_FOLDER) {

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_folder, parent, false);

        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_folder, parent, false);
        }

        return new ViewHolder(view);
    }

    public void swap(List<Folder> folderList){
        if(folderList != null && !folderList.isEmpty()) {
            mFolderList = folderList;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        int realPosition = position - 1;

        //Skip binding the FIRST and LAST item (they are customized
        if (position == 0) {

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickListenerFolderListInteraction(null);
                }
            });

            return;
        }else if(position == ((mFolderList.size() - 1) + ADITIONAL_VIEWS)){
            return; //No view for the last one
        }

        holder.mItem = mFolderList.get(realPosition);

        holder.mContentView.setText(mFolderList.get(realPosition).getDescription());

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (null != mListener) {
                    mListener.onLongClickListenerFolderListInteraction(holder.mItem);
                }

                return false;
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onClickListenerFolderListInteraction(holder.mItem);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFolderList.size() + ADITIONAL_VIEWS; //My Book well be aways as first
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
