package com.fy.baselibrary.rv.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fy.baselibrary.utils.ResUtils;

/**
 * RecycleView LinearLayoutManager 样式分割线
 * Created by fangs on 2017/12/29.
 */
public class ListItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    /**
     * 用于绘制间隔样式
     */
    private Drawable mDivider;

    private Builder builder;

    /**
     * 构造分割线
     * @param context
     */
    public ListItemDecoration(Context context, Builder builder) {
        this.builder = builder;
        init(context);
    }

    private void init(Context context){
        // 获取默认主题的属性
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();

        if (builder.mSpace == 0) builder.mSpace = mDivider.getIntrinsicHeight();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // 获得每个Item的位置
        int itemPosition = parent.getChildAdapterPosition(view);

        // 第1个Item不绘制(此处：不设置间隔)分割线
        if (itemPosition == 0) return;

        if (builder.orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, builder.mSpace, 0, 0);//设置 列表item 四个方向的padding
        } else {
            outRect.set(builder.mSpace, 0, 0, 0);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();

        if (!builder.isDraw) return;//如果不绘制 则直接结束绘制

        //没有子view或者没有没有颜色直接return
        if (null == mDivider || layoutManager.getChildCount() == 0) return;

        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left  = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            // ItemView的下边界：ItemView 的 bottom坐标 + 距离RecyclerView底部距离
            int top = child.getBottom() + params.bottomMargin;

            // 绘制分割线的下边界 = ItemView的下边界+分割线的高度
            int bottom = top + builder.mSpace;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int left  = child.getRight() + params.rightMargin;
            int right = left + builder.mSpace;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


    public static class Builder {
        /**
         * 是否 绘制
         */
        public boolean isDraw = true;

        /**
         * 设置间隔 宽度
         * （如果参数不为 0，则表示 只设置间隔；
         *            为 0，则表示按系统配置的 listDivider 设置间隔，和绘制分割线）单位是dp;
         */
        public int mSpace;

        /**
         * 布局方向
         */
        public int orientation = LinearLayoutManager.VERTICAL;

        public Builder setDraw(boolean draw) {
            isDraw = draw;
            return this;
        }

        public Builder setmSpace(@DimenRes int dimenId) {
            this.mSpace = (int) ResUtils.getDimen(dimenId);
            return this;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        /**
         * 创建 ItemDecoration
         * @param context
         * @return
         */
        public ListItemDecoration create(Context context){
            return new ListItemDecoration(context, this);
        }
    }
}
