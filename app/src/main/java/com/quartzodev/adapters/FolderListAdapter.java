package com.quartzodev.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.Folder;
import com.quartzodev.fragments.FolderListFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Folder} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.ViewHolder> {

    //Add folder is the additional view here
    private static final int ADDITIONAL_VIEWS = 1;

    private static final int VIEW_TYPE_MY_BOOKS = 0;
    private static final int VIEW_TYPE_FOLDER = 1;
    private static final int VIEW_TYPE_ADD_FOLDER = 3;
    private final OnListFragmentInteractionListener mListener;
    //private final List<DummyItem> mValues;
    private List<Folder> mFolderList = new ArrayList<>();

    public FolderListAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return VIEW_TYPE_MY_BOOKS;
        } else if (position == ((mFolderList.size() - 1) + ADDITIONAL_VIEWS)) {
            return VIEW_TYPE_ADD_FOLDER;
        }

        return VIEW_TYPE_FOLDER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_MY_BOOKS) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_books, parent, false);
        } else if (viewType == VIEW_TYPE_ADD_FOLDER) {

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_folder, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_folder, parent, false);
        }

        return new ViewHolder(view);
    }

    public void swap(List<Folder> folderList) {
        if (folderList != null) {
            mFolderList.clear();
            mFolderList.addAll(folderList);
            sortList();
            this.notifyDataSetChanged();
        }
    }

    public void sortList(){
        Collections.sort(mFolderList, new Comparator<Folder>() {
            @Override
            public int compare(Folder f1, Folder f2) {

                if(f1.getDescription() == null || f2.getDescription() == null)
                    return 0;

                return f1.getDescription().compareTo(f2.getDescription());
            }
        });
    }

    //TODO make this flow better. Too confusing!
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //Skip binding the FIRST and LAST item (they are customized
        if (position == 0) {

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickListenerFolderListInteraction(null);
                }
            });

            int totalBooks = 0;
            // Grab the last item which is myBooks section
            if(!mFolderList.isEmpty() && mFolderList.get(mFolderList.size() - 1).getBooks() != null){
                totalBooks = mFolderList.get(mFolderList.size() - 1).getBooks().size();
            }

            holder.mTotalBooks.setText(" (" + totalBooks + ")");

            return;
        } else if (position == ((mFolderList.size() - 1) + ADDITIONAL_VIEWS)) {

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickAddFolderListInteraction();
                }
            });

            return; //No view for the last one
        }

        if(!mFolderList.isEmpty() && position < (mFolderList.size() + ADDITIONAL_VIEWS) - 1) {

            int realPosition = position - 1;

            holder.mItem = mFolderList.get(realPosition);

            holder.mContentView.setText(mFolderList.get(realPosition).getDescription());

            int totalBooks = 0;

            if (mFolderList.get(realPosition).getBooks() != null) {
                totalBooks = mFolderList.get(realPosition).getBooks().size();
            }

            holder.mTotalBooks.setText(" (" + totalBooks + ")");

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

    }

    @Override
    public int getItemCount() {
        return mFolderList.size() + ADDITIONAL_VIEWS; //My Book well be aways as first
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mTotalBooks;
        public Folder mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mTotalBooks = view.findViewById(R.id.totalBooks);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
