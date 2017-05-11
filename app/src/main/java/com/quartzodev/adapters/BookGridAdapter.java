package com.quartzodev.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quartzodev.api.BookApi;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Folder;
import com.quartzodev.fragments.BookGridFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 18/04/2017.
 */

public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.ViewHolder> {


    private Context mContext;
    private List<BookApi> mBookList = new ArrayList<>();
    private BookGridFragment.OnGridFragmentInteractionListener mListener;
    private String mFolderId;

    public BookGridAdapter(Context mContext, List<BookApi> bookList, String folderId, BookGridFragment.OnGridFragmentInteractionListener listener) {
        this.mContext = mContext;
        this.mBookList = bookList;
        this.mFolderId = folderId;
        this.mListener = listener;
    }

    @Override
    public BookGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        final BookGridAdapter.ViewHolder vh = new BookGridAdapter.ViewHolder(view);

        return vh;
    }

    public void swap(Folder folder){
        clearList();
        if(folder != null && folder.getBooks() != null) {
            this.mBookList = new ArrayList<>(folder.getBooks().values());
        }
        notifyDataSetChanged();
    }

    private void clearList(){
        this.mBookList.clear();
    }


    public void swap(List<BookApi> bookApiList){
        mBookList.clear();
        if(bookApiList != null)
            mBookList.addAll(bookApiList);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(BookGridAdapter.ViewHolder holder, int position) {

        final BookApi book = mBookList.get(position);

        holder.textViewBookTitle.setText(book.getVolumeInfo().getTitle());

        holder.textViewBookAuthor.setText(book.getVolumeInfo().getAuthors() == null ? "" : book.getVolumeInfo().getAuthors().get(0));

        if(book.getVolumeInfo().getImageLink() != null) {
            Glide.with(mContext)
                    .load(book.getVolumeInfo().getImageLink().getThumbnail())
                    .centerCrop()
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(holder.ImageViewthumbnail);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickListenerBookGridInteraction(mFolderId,book);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBookList != null ? mBookList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView ImageViewthumbnail;
        @BindView(R.id.book_title)
        TextView textViewBookTitle;
        @BindView(R.id.book_author) TextView textViewBookAuthor;
        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
