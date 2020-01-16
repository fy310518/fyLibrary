package com.fy.baselibrary.utils.drawable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.TextView;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.ResUtils;

/**
 * Tint 是 Android5.0 引入的一个属性，对drawable进行着色
 * Created by fangs on 2018/2/11.
 */
public class TintUtils {

    /**
     * tint这个属性，是ImageView有的，它可以给ImageView的src设置，除了tint 之外，
     * 还有backgroundTint,foregroundTint,drawableTint,它们分别对应对背景、前景、drawable进行着色处理。
     */
    private TintUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 使用 tint改变 drawable 颜色
     * @param drawableId    将要改变的 drawable 的资源 id
     * @param colorId       将要改变的 颜色 id
     * @return
     */
    public static Drawable getTintDrawable(@DrawableRes int drawableId, int drawableType, @ColorRes int colorId) {
        int color = ResUtils.getColor(colorId);
        return getTintColorDrawable(drawableId, drawableType, color);
    }

    /**
     * 使用 tint改变 drawable 颜色
     * @param drawableId    将要改变的 drawable 的资源 id
     * @param color         将要改变的 颜色
     * @return
     */
    public static Drawable getTintColorDrawable(@DrawableRes int drawableId, int drawableType, @ColorInt int color) {
        Drawable drawable = getDrawable(drawableId, drawableType);

        Drawable.ConstantState state = drawable.getConstantState();
        Drawable drawable1 = DrawableCompat
                .wrap(state == null ? drawable : state.newDrawable())
                .mutate();//调用此方法 避免同一界面 展示同一Drawable 所有的Drawable 的颜色都改变

        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable1, color);

        return drawable1;
    }

    /****************优雅的实现背景选择器*******************/
    /*
        1、显示不同的颜色 数组
            int[] colors = new int[]{ContextCompat.getColor(this, R.color.pink),
            ContextCompat.getColor(this, R.color.colorPrimaryDark)};
       /2、View状态数组（比如按下，选中等）
            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_pressed};
            states[1] = new int[]{};

        方式一：
            Drawable drawable2 = tintSelector(drawable, colors, states);
            imageView1.setBackground(drawable2);
        方式二：
            StateListDrawable stateListDrawable = getStateListDrawable(drawable, states);
            Drawable drawable3 = tintSelector(stateListDrawable, colors, states);
            imageView2.setBackground(drawable3);
     */

    /**
     * StateListDrawable 设置背景选择器
     * @param drawable              图片资源（shape,png图片，svg图）
     * @param states                View状态数组（比如按下，选中等）
     * @return StateListDrawable
     */
    public static StateListDrawable getStateListDrawable(Drawable drawable, int[][] states) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        for (int[] state : states) {
            stateListDrawable.addState(state, drawable);
        }
        return stateListDrawable;
    }

    /**
     * Tint 方式实现单图片 背景 selector
     * @param drawable          图片资源（shape, png图片，svg）
     * @param colors            不同状态 显示不同的颜色 数组
     * @param states            View状态数组（比如按下，选中等）
     * @return Drawable
     */
    public static Drawable tintSelector(Drawable drawable, int[] colors, int[][] states) {
        ColorStateList colorList = new ColorStateList(states, colors);

        Drawable.ConstantState state = drawable.getConstantState();
        drawable = DrawableCompat
                .wrap(state == null ? drawable : state.newDrawable())
                .mutate();

        DrawableCompat.setTintList(drawable, colorList);

        return drawable;
    }

    /**
     * StateListDrawable 实现不同状态不同图片的 背景选择器
     * @param drawables
     * @param states
     * @return
     */
    public static StateListDrawable getStateListDrawable(Drawable[] drawables, int[][] states) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        for (int i = 0; i < drawables.length; i++) {
            int[] state = states[i];
            Drawable drawable = drawables[i];
            stateListDrawable.addState(state, drawable);
        }
        return stateListDrawable;
    }


    /**
     * 对TextView 设置不同状态时其文字颜色
     */
    private ColorStateList getColorStateList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[] { pressed, focused, normal, focused, normal };
        int[][] states = new int[5][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
        states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };
        states[2] = new int[] { android.R.attr.state_enabled };
        states[3] = new int[] { android.R.attr.state_focused };
        states[4] = new int[] {};

        return new ColorStateList(states, colors);
    }


    /**
     * 获取 指定 ID 的 drawable 资源
     * @param draId
     * @param drawableType drawable 类型（0：png、shape 图标等；1：svg 图标；2：动画 svg 图标）
     * @return 返回的 drawable 注意空指针
     */
    public static Drawable getDrawable(@DrawableRes int draId, int drawableType) {
        Drawable drawable = null;
        Context ctx = ConfigUtils.getAppCtx();

        switch (drawableType) {
            case 0:
                //png、shape 图等
                drawable = ContextCompat.getDrawable(ctx, draId);
                break;
            case 1:
                //vector图标
                drawable = VectorDrawableCompat.create(ctx.getResources(), draId, ctx.getTheme());
                break;
            case 2:
                //动态svg 图标
                drawable = AnimatedVectorDrawableCompat.create(ctx, draId);
                break;
        }

        return drawable;
    }

    /**
     * 设置icon 在TextView的位置
     * @param tv
     * @param drawable
     * @param position 1、2、3、4 分别对应：左、上、右、下
     */
    public static void setTxtIconLocal(TextView tv, Drawable drawable, int position){
        drawable.setBounds(0, 0,
                drawable.getMinimumWidth(), drawable.getMinimumHeight());

        switch (position) {
            case 1:
                tv.setCompoundDrawables(drawable, null, null, null);
                break;
            case 2:
                tv.setCompoundDrawables(null, drawable, null, null);
                break;
            case 3:
                tv.setCompoundDrawables(null, null, drawable, null);
                break;
            case 4:
                tv.setCompoundDrawables(null, null, null, drawable);
                break;
        }
    }
}
