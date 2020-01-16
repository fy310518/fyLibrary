package com.fy.baselibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * 实现item单选、多选效果
 * Created by 下载 on 2016/6/15.
 */
public class CheckableLayout extends RelativeLayout implements Checkable {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean mChecked;

    public CheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setChecked(boolean b) {

        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {

        setChecked(!mChecked);
    }


    @Override
    protected int[] onCreateDrawableState(int extraSpace) {

        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        if (isChecked()) mergeDrawableStates(drawableState, CHECKED_STATE_SET);

        return drawableState;
    }
}
