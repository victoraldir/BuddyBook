package com.quartzodev.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.quartzodev.buddybook.GlideApp;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.utils.TextUtils;
import com.quartzodev.views.DynamicImageView;

import java.util.List;

/**
 * Created by victoraldir on 18/04/2017.
 */

public class BookGridAdapterFirebase extends FirebaseRecyclerAdapter<Book, BookGridViewHolder> {

    private final int POS_BOOK_LENT = 1;
    private final int POS_BOOK_AVAILABLE = 2;
    private final int POS_BOOK_CUSTOM_LENT = 3;
    private final int POS_BOOK_CUSTOM_AVAILABLE = 4;

    private Context mContext;
    private ILoading mLoading;
    private BookGridFragment.OnGridFragmentInteractionListener mListener;
    private String mFolderId;
    private Integer mMenuId;
    private int mLastPosition = -1;

    public BookGridAdapterFirebase(FirebaseRecyclerOptions<Book> options,
                                   ILoading iLoading,
                                   BookGridFragment.OnGridFragmentInteractionListener listener,
                                   String folderId,
                                   Integer menuId,
                                   Context context) {
        super(options);
        mContext = context;
        mLoading = iLoading;
        mListener = listener;
        mFolderId = folderId;
        mMenuId = menuId;

    }

    @Override
    protected void onBindViewHolder(final BookGridViewHolder holder, int i, final Book book) {
        try {

//            final Book book = new ArrayList<>(mBookList).get(position);

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
            setAnimation(holder.itemView, i);

        }catch (Exception ex){
            Log.wtf("TAG",ex.getMessage());
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemViewType(int position) {

        Book book = getItem(position);

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

    @Override
    public BookGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        final BookGridViewHolder vh = new BookGridViewHolder(view);

        return vh;
    }


    @Override
    public boolean onFailedToRecycleView(BookGridViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onBindViewHolder(BookGridViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(BookGridViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        mLoading.setLoading(false);
    }

    @Override
    public void onError(DatabaseError error) {
        Log.e("BookGridAdapterFirebase", error.getDetails());
        super.onError(error);
    }

    public interface ILoading{
       void setLoading(boolean flag);
    }
}
