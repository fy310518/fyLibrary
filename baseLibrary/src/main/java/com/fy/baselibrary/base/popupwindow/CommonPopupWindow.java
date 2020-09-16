package com.fy.baselibrary.base.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.fy.baselibrary.base.PopupDismissListner;
import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.base.dialog.CommonDialog;
import com.fy.baselibrary.dress.DressColor;
import com.fy.baselibrary.dress.DressUtils;
import com.fy.baselibrary.utils.DensityUtils;
import com.fy.baselibrary.utils.ScreenUtils;

/**
 * popupWindow 封装
 * Created by fangs on 2018/3/21.
 */
public abstract class CommonPopupWindow extends PopupWindow {

    protected Context mContext;
    @LayoutRes
    protected int layoutId;
    View view;

    /** 宽度，高度 -1(ViewGroup.LayoutParams.MATCH_PARENT)：撑满；-2(ViewGroup.LayoutParams.WRAP_CONTENT)：自适应； 其他固定数值 */
    int mWidth = -2, mHeight = -2;//弹窗的宽和高
    /** 宽度 百分比（如：屏幕宽度 的 50%）*/
    protected int widthPercent = -1;

    boolean isShowAnim;
    int anim;//动画Id
    boolean isHide = true;
    float bgAlpha = 0.5f;

    PopupDismissListner dismissListner;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    public int getWidth() {
        return view.getMeasuredWidth();
    }

    @Override
    public int getHeight() {
        return view.getMeasuredHeight();
    }

    /** 设置 PopupWindow 布局 */
    protected abstract int initLayoutId();

    /** 渲染数据到View中 */
    public abstract void convertView(ViewHolder holder);

    public CommonPopupWindow() {}

    /**
     * 绘制 Popup UI
     * 数据构建完成后，必须调用此方法，不然popupWindow 没有内容
     * @param context
     */
    public CommonPopupWindow onCreateView(Context context) {
        mContext = context;

        layoutId = initLayoutId();

        view = LayoutInflater.from(context).inflate(layoutId, null);
        DensityUtils.measureWidthAndHeight(view);

        convertView(ViewHolder.createViewHolder(context, view));

        initParams(view);

        //使用 colorMatrix
        DressColor dressColor = DressUtils.getDressColor(context);
        if (null != dressColor){
            ColorMatrix cm = dressColor.getColorMatrix();
            Paint rootPaint = new Paint();
            rootPaint.setColorFilter(new ColorMatrixColorFilter(cm));
            view.setLayerType(View.LAYER_TYPE_HARDWARE, rootPaint);
        }
        return this;
    }

    /**
     * 初始化 PopupWindow 样式
     */
    protected void initParams(View view) {
        setContentView(view);

        if (widthPercent > 0){
            setWidth(ScreenUtils.getScreenWidth() * widthPercent / 100);
        } else {
            setWidth(mWidth > 0 ?   DensityUtils.dp2px(mWidth)  : mWidth);
        }
        setHeight(mHeight > 0 ? DensityUtils.dp2px(mHeight) : mHeight);

        //设置动画
        if (isShowAnim)setAnimationStyle(anim);
        setHide(isHide);
    }

    /**
     * 点击 window外的区域 是否消失
     * @param touchable 是否可点击
     */
    private void setHide(boolean touchable) {
        setBackgroundDrawable(new ColorDrawable(0x000000));//设置透明背景
        setOutsideTouchable(touchable);//设置outside可点击
        setFocusable(touchable);
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    private void bgAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bgAlpha(1.0f);// popupWindow隐藏时恢复屏幕正常透明度
        if (null != dismissListner) dismissListner.onDismiss();
    }

    @Override
    public void showAsDropDown(View anchor) {
        popupShowAdapter(anchor);
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        popupShowAdapter(anchor);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    /** 适配 7.0 以上版本 popupwindow 显示全屏问题 */
    private void popupShowAdapter(View anchor){
        if(Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
//            setHeight(h);
            setHeight(mHeight > 0 ? DensityUtils.dp2px(mHeight) : mHeight);
        }
    }



    /**
     * 设置添加屏幕的背景透明度
     * @return
     */
    public CommonPopupWindow bgAlpha() {
        bgAlpha(bgAlpha);
        return this;
    }

    /**
     * 设置 弹窗宽度占屏幕百分比
     * @param widthPercent 如：80、70
     * @return
     */
    public CommonPopupWindow setWidthPercent(int widthPercent) {
        this.widthPercent = widthPercent;
        return this;
    }

    /**
     * 设置宽度和高度 如果不设置 默认是wrap_content
     *
     * @param width  宽(dp)
     * @param height 高(dp)
     * @return Builder
     */
    public CommonPopupWindow setWidthAndHeight(int width, int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    /**
     * 是否可点击Outside消失
     *
     * @param touchable 是否可点击
     * @return CommonPopupWindow
     */
    public CommonPopupWindow setOutside(boolean touchable) {
        isHide = touchable;
        return this;
    }

    /**
     * 设置 popupWindow 背景透明度
     * @param bgAlpha  0.0-1.0   0表示完全透明
     * @return
     */
    public CommonPopupWindow setBgAlpha(float bgAlpha) {
        this.bgAlpha = bgAlpha;
        return this;
    }

    /**
     * 设置进出动画（声明的动画样式 id）
     * @return CommonPopupWindow
     */
    public CommonPopupWindow setAnim(@StyleRes int animID) {
        isShowAnim = true;
        this.anim = animID;
        return this;
    }

    /**
     * 设置 窗口 dismiss 监听
     *
     * @param dismissListner
     * @return
     */
    public CommonPopupWindow setDismissListner(PopupDismissListner dismissListner) {
        this.dismissListner = dismissListner;
        return this;
    }

}
