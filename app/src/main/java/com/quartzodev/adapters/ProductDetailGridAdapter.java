package com.quartzodev.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.VolumeInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 18/09/2017.
 */

public class ProductDetailGridAdapter extends RecyclerView.Adapter<ProductDetailGridAdapter.ViewHolder> {

    private static final int NUM_CELLS = 4;

    private static final int POS_NUM_PAGES = 0;
    private static final int POS_ISBN = 1;
    private static final int POS_LANGUAGE = 2;
    private static final int POS_PRINT_TYPE = 3;

    private VolumeInfo mData;
    private Context mContext;

    public ProductDetailGridAdapter(VolumeInfo volumeInfo, Context context){
        mData = volumeInfo;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item,viewGroup,false);

        final ProductDetailGridAdapter.ViewHolder vh = new ProductDetailGridAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        switch (i){
            case POS_ISBN:
                if(mData.getIsbn10() != null){
                    if(mData.getIsbn13() != null){
                        viewHolder.mTextView.setText(mData.getIsbn10() + "/" + mData.getIsbn13());
                    }else{
                        viewHolder.mTextView.setText(mData.getIsbn10());
                    }
                }else if(mData.getIsbn13() != null){
                    viewHolder.mTextView.setText(mData.getIsbn13());
                }

                viewHolder.mImageView.setImageResource(R.drawable.ic_action_barcode_1);

                break;
            case POS_LANGUAGE:

                viewHolder.mTextView.setText(mData.getLanguage());
                viewHolder.mImageView.setImageResource(R.drawable.ic_language);

                break;
            case POS_NUM_PAGES:

                viewHolder.mTextView.setText(String.format(mContext.getString(R.string.pages_details),mData.getPageCount()));
                viewHolder.mImageView.setImageResource(R.drawable.ic_action_book);

                break;
            case POS_PRINT_TYPE:

                viewHolder.mTextView.setText(mData.getPrintType());
                viewHolder.mImageView.setImageResource(R.drawable.ic_print);

                break;
            default:

                viewHolder.mTextView.setText("Test");
                viewHolder.mImageView.setImageResource(R.drawable.ic_folder);

                break;
        }

    }

    @Override
    public int getItemCount() {
        return NUM_CELLS;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.info_text)
        TextView mTextView;

        @BindView(R.id.icon_text_info)
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
