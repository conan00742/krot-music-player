package com.example.krot.musicplayer.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Krot on 2/8/18.
 */

public class SongBackgroundImageView extends android.support.v7.widget.AppCompatImageView {

    private int width = 1;
    private int height = 1;

    public SongBackgroundImageView(Context context) {
        super(context);
    }

    public SongBackgroundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SongBackgroundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int customWidth = MeasureSpec.getSize(widthMeasureSpec);
        int customHeight = customWidth;
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(customWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(customHeight, MeasureSpec.EXACTLY));
    }
}
