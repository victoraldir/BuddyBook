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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 18/09/2017.
 */

public class ProductDetailGridAdapter extends RecyclerView.Adapter<ProductDetailGridAdapter.ViewHolder> {

    private static final int CELL_ISBN = 0;
    private static final int CELL_LANGUAGE = 1;
    private static final int CELL_NUM_PAGES = 2;
    private static final int CELL_PRINT_TYPE = 3;

    private List<Integer> listCells = new ArrayList<>();

    private VolumeInfo mData;
    private Context mContext;

    public ProductDetailGridAdapter(VolumeInfo volumeInfo, Context context){
        mData = volumeInfo;
        mContext = context;
        countCellByVolumeInfo(mData);
    }

    private void countCellByVolumeInfo(VolumeInfo volumeInfo){
        if(volumeInfo.getIsbn13() != null || volumeInfo.getIsbn10() != null){
            listCells.add(CELL_ISBN);
        }

        if(volumeInfo.getLanguage() != null){
            listCells.add(CELL_LANGUAGE);
        }

        if(volumeInfo.getPrintType() != null){
            listCells.add(CELL_PRINT_TYPE);
        }

        if(volumeInfo.getPageCount() != null){
            listCells.add(CELL_NUM_PAGES);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item,viewGroup,false);

        final ProductDetailGridAdapter.ViewHolder vh = new ProductDetailGridAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        switch (listCells.get(i)){
            case CELL_ISBN:
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
            case CELL_LANGUAGE:

                viewHolder.mTextView.setText(mData.getLanguage());
                viewHolder.mImageView.setImageResource(R.drawable.ic_language);

                break;
            case CELL_NUM_PAGES:

                viewHolder.mTextView.setText(String.format(mContext.getString(R.string.pages_details),mData.getPageCount()));
                viewHolder.mImageView.setImageResource(R.drawable.ic_action_book);

                break;
            case CELL_PRINT_TYPE:

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
        return listCells.size();
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
