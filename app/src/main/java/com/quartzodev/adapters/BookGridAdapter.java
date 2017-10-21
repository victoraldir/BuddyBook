package com.quartzodev.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.quartzodev.buddybook.GlideApp;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.utils.TextUtils;
import com.quartzodev.views.DynamicImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 18/04/2017.
 */

public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.ViewHolder> {

    private final int POS_BOOK_LENT = 1;
    private final int POS_BOOK_AVAILABLE = 2;
    private final int POS_BOOK_CUSTOM_LENT = 3;
    private final int POS_BOOK_CUSTOM_AVAILABLE = 4;

    private Context mContext;
    private List<Book> mBookList = new ArrayList<>();
    private BookGridFragment.OnGridFragmentInteractionListener mListener;
    private String mFolderId;
    private int mMenuId;

    /**
     * Here is the key method to apply the animation
     */
    private int lastPosition = -1;

    public BookGridAdapter(Context mContext,
                           List<Book> bookList,
                           BookGridFragment.OnGridFragmentInteractionListener listener,
                           int menuId) {
        this.mContext = mContext;
        this.mBookList = bookList;
        this.mListener = listener;
        this.mMenuId = menuId;
    }

    @Override
    public BookGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == POS_BOOK_LENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_lent, parent, false);
        } else if (viewType == POS_BOOK_CUSTOM_LENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_custom, parent, false);
        } else if (viewType == POS_BOOK_CUSTOM_AVAILABLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_custom_available, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        }

        final BookGridAdapter.ViewHolder vh = new BookGridAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public int getItemViewType(int position) {

        final Book book = new ArrayList<>(mBookList).get(position);

        if(mFolderId == null){
            return 0;
        }

        if(book.getLend() != null){
            if(book.isCustom()){
                return POS_BOOK_CUSTOM_LENT;
            }
            return POS_BOOK_LENT;
        }else if (book.isCustom()) {
            return POS_BOOK_CUSTOM_AVAILABLE;
        }

        return POS_BOOK_AVAILABLE;
    }

    public void setFolderId(String folderId) {
        clearList();
        mFolderId = folderId;
    }

    public int removeItem(Book bookApi) {

        int mIndexItemRemoved = 0;

        if (mBookList != null) {
            mIndexItemRemoved = mBookList.indexOf(bookApi);
            mBookList.remove(bookApi);
            notifyDataSetChanged();
        }

        return mIndexItemRemoved;
    }

    public void addItem(int index, Book bookApi) {
        if (mBookList != null && !mBookList.contains(bookApi)) {
            mBookList.add(index,bookApi);
            notifyDataSetChanged();
        }
    }

    private void clearList() {
        if(mBookList != null) {
            this.mBookList.clear();
            this.notifyDataSetChanged();
        }
    }

    public void swap(List<Book> bookApiList) {
        if (bookApiList != null) {
            bookApiList.removeAll(mBookList);
            if(!bookApiList.isEmpty()) {
                if(!mBookList.isEmpty()) {
                    for (Book book1 : bookApiList) {
                        for (Book book2 : mBookList) {
                            if(book1.getId().equals(book2.getId())){
                                int position = mBookList.indexOf(book2);
                                mBookList.remove(position);
                                mBookList.add(position,book1);
                                break;
                            }
                        }
                    }
                }else{
                    mBookList.addAll(bookApiList);
                }

                this.notifyDataSetChanged();
            }
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(final BookGridAdapter.ViewHolder holder, int position) {

        try {

            final Book book = new ArrayList<>(mBookList).get(position);

            holder.textViewBookTitle.setText(book.getVolumeInfo().getTitle());
            holder.textViewBookAuthor.setText(book.getVolumeInfo().getAuthors() == null ? "" : book.getVolumeInfo().getAuthors().get(0));

            holder.toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickListenerBookGridInteraction(mFolderId, book, (DynamicImageView) holder.imageViewthumbnail);
                }
            });

            if (!holder.toolbar.getMenu().hasVisibleItems()) {
                holder.toolbar.inflateMenu(mMenuId);
                if (book.getLend() != null && holder.toolbar.getMenu().findItem(R.id.action_lend) != null) {
                    holder.toolbar.getMenu().findItem(R.id.action_lend)
                            .setTitle(mContext.getString(R.string.action_return_lend));
                }
            }

            holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    int menuId = item.getItemId();

                    switch (menuId) {
                        case R.id.action_delete:
                            mListener.onDeleteBookClickListener(mFolderId, book);
                            break;
                        case R.id.action_move_folder:
                            mListener.onAddBookToFolderClickListener(mFolderId, book);
                            break;
                        case R.id.action_copy:
                            mListener.onCopyBookToFolderClickListener(mFolderId, book);
                            break;
                        case R.id.action_lend:
                            if (item.getTitle().equals(mContext.getString(R.string.action_return_lend))) {
                                mListener.onReturnBookClickListener(book);
                            } else {
                                mListener.onLendBookClickListener(book, item);
                            }
                            break;
                    }

                    return false;
                }
            });

            if (book.getVolumeInfo().getImageLink() != null) {

                GlideApp.with(mContext)
                        .load(book.getVolumeInfo().getImageLink().getThumbnail())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageViewthumbnail);

                holder.imageViewthumbnail.setContentDescription(
                        String.format(mContext.getString(R.string.cover_book_cd), book.getVolumeInfo()
                                .getTitle()));

            } else if (book.isCustom()) {

                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(TextUtils.getFirstLetterTitle(book), Color.BLUE);

                holder.imageViewthumbnail.setContentDescription(
                        String.format(mContext.getString(R.string.cover_book_cd), book.getVolumeInfo()
                                .getTitle()));

                holder.imageViewthumbnail.setImageDrawable(drawable);


            } else {

                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(TextUtils.getFirstLetterTitle(book), Color.RED);

                holder.imageViewthumbnail.setImageDrawable(drawable);


            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickListenerBookGridInteraction(mFolderId, book, (DynamicImageView) holder.imageViewthumbnail);
                }
            });

            // call Animation function
            setAnimation(holder.itemView, position);

        }catch (Exception ex){
            Log.wtf("TAG",ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mBookList != null ? mBookList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;

        @BindView(R.id.thumbnail)
        ImageView imageViewthumbnail;

        @BindView(R.id.book_toolbar)
        Toolbar toolbar;

        @BindView(R.id.book_title)
        TextView textViewBookTitle;

        @BindView(R.id.book_author)
        TextView textViewBookAuthor;

        @BindView(R.id.icon_book_lend)
        ImageView containerIconLend;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
