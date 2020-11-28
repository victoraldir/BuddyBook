package com.quartzodev.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.quartzodev.buddybook.GlideApp;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.utils.TextUtils;
import com.quartzodev.views.DynamicImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraldir on 18/04/2017.
 */

public class BookGridAdapter extends RecyclerView.Adapter<BookGridViewHolder> {

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
    public BookGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);

//        if (viewType == POS_BOOK_LENT) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_lent, parent, false);
//        } else if (viewType == POS_BOOK_CUSTOM_LENT) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_custom, parent, false);
//        } else if (viewType == POS_BOOK_CUSTOM_AVAILABLE) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_custom_available, parent, false);
//        } else {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
//        }

        return new BookGridViewHolder(view);
    }

//    @Override
//    public int getItemViewType(int position) {
//
//        final Book book = new ArrayList<>(mBookList).get(position);
//
//        if(mFolderId == null){
//            return 0;
//        }
//
//        if(book.getLend() != null){
//            if(book.isCustom()){
//                return POS_BOOK_CUSTOM_LENT;
//            }
//            return POS_BOOK_LENT;
//        }else if (book.isCustom()) {
//            return POS_BOOK_CUSTOM_AVAILABLE;
//        }
//
//        return POS_BOOK_AVAILABLE;
//    }

    public void setFolderId(String folderId) {
        mFolderId = folderId;
    }

    public void swap(List<Book> bookApiList) {
        if (bookApiList != null) {

            if (bookApiList.isEmpty()) {
                mBookList.clear();
            } else {
                mBookList.clear();
                mBookList.addAll(bookApiList);
            }

            this.notifyDataSetChanged();
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
    public void onBindViewHolder(final BookGridViewHolder holder, int position) {

        try {

            final Book book = new ArrayList<>(mBookList).get(position);

            holder.textViewBookTitle.setText(book.getVolumeInfo().getTitle());
            holder.textViewBookAuthor.setText(book.getVolumeInfo().getAuthors() == null ? "" : book.getVolumeInfo().getAuthors().get(0));

            holder.toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickListenerBookGridInteraction(mFolderId, book, (DynamicImageView) holder.imageViewThumbnail);
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
                        .into(holder.imageViewThumbnail);

                holder.imageViewThumbnail.setContentDescription(
                        String.format(mContext.getString(R.string.cover_book_cd), book.getVolumeInfo()
                                .getTitle()));

            } else if (book.isCustom()) {

                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(TextUtils.getFirstLetterTitle(book), Color.BLUE);

                holder.imageViewThumbnail.setContentDescription(
                        String.format(mContext.getString(R.string.cover_book_cd), book.getVolumeInfo()
                                .getTitle()));

                holder.imageViewThumbnail.setImageDrawable(drawable);


            } else {

                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(TextUtils.getFirstLetterTitle(book), Color.RED);

                holder.imageViewThumbnail.setImageDrawable(drawable);


            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickListenerBookGridInteraction(mFolderId, book, (DynamicImageView) holder.imageViewThumbnail);
                }
            });

            // call Animation function
            setAnimation(holder.itemView, position);

        } catch (Exception ex) {
            Log.wtf("TAG", ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mBookList != null ? mBookList.size() : 0;
    }

}
