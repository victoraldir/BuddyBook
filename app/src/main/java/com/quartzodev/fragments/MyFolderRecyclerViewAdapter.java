package com.quartzodev.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quartzodev.buddybook.R;
import com.quartzodev.fragments.FolderFragment.OnListFragmentInteractionListener;
import com.quartzodev.fragments.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFolderRecyclerViewAdapter extends RecyclerView.Adapter<MyFolderRecyclerViewAdapter.ViewHolder> {

    private static final int VIEW_TYPE_MY_BOOKS = 1;
    private static final int VIEW_TYPE_FOLDER = 2;
    private static final int VIEW_TYPE_ADD_FOLDER = 3;

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    // Flag to determine if we want to use a separate view for "My Books"
    private boolean mMyBooksLayout = true;

    public MyFolderRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        if(position  == 0){
            return VIEW_TYPE_MY_BOOKS ;
        }else if (position == mValues.size()){
            return VIEW_TYPE_ADD_FOLDER;
        }

        return VIEW_TYPE_FOLDER;
    }

//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        // Choose the layout type
//        int viewType = getItemViewType(cursor.getPosition());
//        int layoutId = -1;
//        switch (viewType) {
//            case VIEW_TYPE_TODAY: {
//                layoutId = R.layout.list_item_forecast_today;
//                break;
//            }
//            case VIEW_TYPE_FUTURE_DAY: {
//                layoutId = R.layout.list_item_forecast;
//                break;
//            }
//        }
//
//        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
//
//        ViewHolder viewHolder = new ViewHolder(view);
//        view.setTag(viewHolder);
//
//        return view;
//    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;

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
