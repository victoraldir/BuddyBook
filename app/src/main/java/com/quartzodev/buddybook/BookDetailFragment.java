package com.quartzodev.buddybook;

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
import com.quartzodev.data.Book;
import com.quartzodev.task.FetchMoviesTask;
import com.quartzodev.transform.CircleTransform;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int LOADER_ID_LIST_BOOKS = 1;

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;
    private Adapter mAdapter;


    public static BookDetailFragment newInstance(long movieId) {
        return new BookDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_LIST_BOOKS,null,this).forceLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_content_main, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new Adapter(getActivity(),new ArrayList<Book>());
        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        return rootView;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesTask(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        mAdapter.swap(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private Context mContext;
        private List<Book> bookList;

        public Adapter(Context mContext, List<Book> bookList) {
            this.mContext = mContext;
            this.bookList = bookList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.item_book, parent, false);
            final ViewHolder vh = new ViewHolder(view);

            return vh;
        }

        public void swap(List<Book> bookList){
            if(bookList != null) {
                this.bookList = bookList;
                notifyDataSetChanged();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Book book = bookList.get(position);
            Book.VolumeInfo bookVolumeInfo = book.volumeInfo;

            holder.textViewBookTitte.setText(bookVolumeInfo.title);
            if(bookVolumeInfo.authors != null)
            holder.textViewBookAuthor.setText(bookVolumeInfo.authors.get(0));

            if(book.volumeInfo.imageLink != null) {
                Glide.with(mContext)
                        .load(book.volumeInfo.imageLink.thumbnail)
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
