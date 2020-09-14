package com.fy.baselibrary.dress.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.fy.baselibrary.dress.DressColor;
import com.fy.baselibrary.dress.DressUtils;

/**
 * description 包裹 指定的布局，进行 颜色矩阵 操作
 * Created by fangs on 2020/9/14 15:42.
 */
public class DressFrameLayout extends FrameLayout {

    private Paint mPaint;

    public DressFrameLayout(Context context) {
        super(context);
    }

    public DressFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        DressColor dressColor = DressUtils.getDressColor(context);
        if (null != dressColor){
            ColorMatrix cm = dressColor.getColorMatrix();
            mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        }
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

}
