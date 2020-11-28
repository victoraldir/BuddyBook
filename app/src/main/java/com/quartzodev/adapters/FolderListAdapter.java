package com.quartzodev.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.fragments.FolderListFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Folder} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class FolderListAdapter extends FirebaseRecyclerAdapter<Folder, FolderListAdapter.ViewHolder> {

    //Add folder is the additional view here
    private static final int ADDITIONAL_VIEWS = 1;

    private static final int VIEW_TYPE_MY_BOOKS = 0;
    private static final int VIEW_TYPE_FOLDER = 1;
    private static final int VIEW_TYPE_ADD_FOLDER = 3;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    public FolderListAdapter(FirebaseRecyclerOptions<Folder> options,
                             OnListFragmentInteractionListener listener,
                             Context context) {
        super(options);
        mListener = listener;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_FOLDER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_folder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder holder, int position, final Folder folder) {

        holder.mItem = getSnapshots().get(position);

        holder.mContentView.setText(getSnapshots().get(position).getDescription());

        int totalBooks = 0;

        if (getSnapshots().get(position).getBooks() != null) {
            totalBooks = getSnapshots().get(position).getBooks().size();
        }

        holder.mTotalBooks.setText(" (" + totalBooks + ")");

        holder.mView.setTag(getRef(position).getKey());

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (null != mListener) {
                    if (v.getTag().equals(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER)) {
                        Toast.makeText(mContext, "You can't delete my books folder", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        mListener.onLongClickListenerFolderListInteraction(holder.mItem);
                    }
                }
                return false;
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {

                    if (view.getTag().equals(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER)) {
                        mListener.onClickListenerFolderListInteraction(null);
                    } else {
                        mListener.onClickListenerFolderListInteraction(holder.mItem);
                    }


                }
            }
        });

//        if (position == 0) {
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onClickListenerFolderListInteraction(null);
//                }
//            });
//
//            int totalBooks = 0;
//            // Grab the last item which is myBooks section
//            if(!getSnapshots().isEmpty() && getSnapshots().get(getSnapshots().size() - 1).getBooks() != null){
//                totalBooks = getSnapshots().get(getSnapshots().size() - 1).getBooks().size();
//            }
//
//            holder.mTotalBooks.setText(" (" + totalBooks + ")");
//
//            return;
//        } else if (position == ((getSnapshots().size() - 1) + ADDITIONAL_VIEWS)) {
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onClickAddFolderListInteraction();
//                }
//            });
//
//            return; //No view for the last one
//        }
//
//        if(!getSnapshots().isEmpty() && position < (getSnapshots().size() + ADDITIONAL_VIEWS) - 1) {
//
//            int realPosition = position - 1;
//
//            holder.mItem = getSnapshots().get(realPosition);
//
//            holder.mContentView.setText(getSnapshots().get(realPosition).getDescription());
//
//            int totalBooks = 0;
//
//            if (getSnapshots().get(realPosition).getBooks() != null) {
//                totalBooks = getSnapshots().get(realPosition).getBooks().size();
//            }
//
//            holder.mTotalBooks.setText(" (" + totalBooks + ")");
//
//            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    if (null != mListener) {
//                        mListener.onLongClickListenerFolderListInteraction(holder.mItem);
//                    }
//
//                    return false;
//                }
//            });
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null != mListener) {
//                        mListener.onClickListenerFolderListInteraction(holder.mItem);
//                    }
//                }
//            });
//        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        mListener.onFolderListIsAvailable(getSnapshots(), getmFolderListCommaSeparated());
    }

    public String getmFolderListCommaSeparated() {

        List<String> stringList = new ArrayList<>();

        for (Folder folder : getSnapshots()) {
            if (folder.getDescription() != null) {
                if (folder.getDescription().equals(mContext.getString(R.string.tab_my_books))) {
                    stringList.add(mContext.getString(R.string.tab_my_books) + "=" + "myBooksFolder");
                } else {
                    stringList.add(folder.getDescription() + "=" + folder.getId());
                }
            }

        }

        return android.text.TextUtils.join(",", stringList);
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
