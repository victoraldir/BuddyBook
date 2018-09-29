package com.quartzodev.views;

/**
 * Created by victoraldir on 11/05/2017.
 */

import android.content.Context;
import android.util.AttributeSet;

public class DynamicImageView extends androidx.appcompat.widget.AppCompatImageView {

    private static final float ASPECT_RATIO = 1.5f;

    public DynamicImageView(Context context) {
        super(context);
    }

    public DynamicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * ASPECT_RATIO);
        setMeasuredDimension(width, height);
    }
}
