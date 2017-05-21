package com.quartzodev.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quartzodev.api.BookApi;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.fragments.ViewPagerFragment;
import com.quartzodev.views.DynamicImageView;

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
    private int mType;
    private BookGridFragment mParent;

    public BookGridAdapter(Context mContext, List<BookApi> bookList,
                           String folderId,
                           BookGridFragment.OnGridFragmentInteractionListener listener,
                           int type,
                           BookGridFragment parent) {
        this.mContext = mContext;
        this.mBookList = bookList;
        this.mFolderId = folderId;
        this.mListener = listener;
        mParent = parent;
        mType = type;
    }

    @Override
    public BookGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        final BookGridAdapter.ViewHolder vh = new BookGridAdapter.ViewHolder(view);

        return vh;
    }

    public void setFolderId(String folderId){
        clearList();
        mFolderId = folderId;
    }

    public void swap(Folder folder) {
        clearList();
        if (folder != null && folder.getBooks() != null) {
            this.mBookList = new ArrayList<>(folder.getBooks().values());
        }
        notifyDataSetChanged();
    }

    public void removeItem(BookApi bookApi){
        if(mBookList != null){
            mBookList.remove(bookApi);
            notifyDataSetChanged();
            updateAdapterParent();
        }
    }

    public void addItem(BookApi bookApi){
        if(mBookList != null && !mBookList.contains(bookApi)){
            mBookList.add(bookApi);
            notifyDataSetChanged();
            updateAdapterParent();
        }
    }

    public void updateAdapterParent(){
        if(mParent != null && mParent.getParentFragment() != null) {
            ((ViewPagerFragment) mParent.getParentFragment()).forceNotify();
        }
    }

    private void clearList() {
        this.mBookList.clear();
    }


    public void swap(List<BookApi> bookApiList) {
        mBookList.clear();
        if (bookApiList != null)
            mBookList.addAll(bookApiList);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final BookGridAdapter.ViewHolder holder, int position) {

        final BookApi book = mBookList.get(position);

        if(mType == BookGridFragment.FLAG_MY_BOOKS_FOLDER && book.getLend() != null){
            holder.containerIconLend.setVisibility(View.VISIBLE);
        }

        holder.textViewBookTitle.setText(book.getVolumeInfo().getTitle());
        holder.textViewBookAuthor.setText(book.getVolumeInfo().getAuthors() == null ? "" : book.getVolumeInfo().getAuthors().get(0));

        holder.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickListenerBookGridInteraction(mFolderId, book, (DynamicImageView) holder.imageViewthumbnail);
            }
        });

        if(!holder.toolbar.getMenu().hasVisibleItems()) {
            switch (mType){
                case BookGridFragment.FLAG_CUSTOM_FOLDER:
                    holder.toolbar.inflateMenu(R.menu.menu_folders);
                    break;
                case BookGridFragment.FLAG_MY_BOOKS_FOLDER:
                    if(book.getLend() == null){
                        holder.toolbar.inflateMenu(R.menu.menu_my_books);
                    }else{
                        holder.toolbar.inflateMenu(R.menu.menu_my_books_return_book);
                    }

                    break;
                case BookGridFragment.FLAG_TOP_BOOKS_FOLDER:
                    break;
            }
        }

        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int menuId = item.getItemId();

                switch (menuId){
                    case R.id.action_delete:
                        mListener.onDeleteBookClickListener(mFolderId,book);
                        break;
                    case R.id.action_have_this:
                        mListener.onAddBookToFolderClickListener(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER,book);
                        break;
                    case R.id.action_move_folder:
                        mListener.onAddBookToFolderClickListener(mFolderId,book);
                        break;
                    case R.id.action_copy:
                        mListener.onCopyBookToFolderClickListener(mFolderId,book);
                        break;
                    case R.id.action_lend:
                        mListener.onLendBookClickListener(book);
                        break;
                    case R.id.action_return_lend:
                        mListener.onReturnBookClickListener(book);
                        break;
                }

                return false;
            }
        });

        if (book.getVolumeInfo().getImageLink() != null) {
            Glide.with(mContext)
                    .load(book.getVolumeInfo().getImageLink().getThumbnail())
                    .centerCrop()
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(holder.imageViewthumbnail);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickListenerBookGridInteraction(mFolderId, book, (DynamicImageView) holder.imageViewthumbnail);
            }
        });

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
        FrameLayout containerIconLend;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
