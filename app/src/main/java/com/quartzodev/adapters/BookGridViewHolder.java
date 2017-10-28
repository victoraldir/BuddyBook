package com.quartzodev.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quartzodev.buddybook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victor on 23/10/17.
 */

public class BookGridViewHolder extends RecyclerView.ViewHolder {

    public final View view;

    @BindView(R.id.thumbnail)
    ImageView imageViewThumbnail;

    @BindView(R.id.book_toolbar)
    Toolbar toolbar;

    @BindView(R.id.book_title)
    TextView textViewBookTitle;

    @BindView(R.id.book_author)
    TextView textViewBookAuthor;

    @BindView(R.id.icon_book_lend)
    ImageView containerIconLend;


    public BookGridViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, itemView);
    }

}
