package com.fy.baselibrary.base.dialog;

import android.content.DialogInterface;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.fy.baselibrary.R;
import com.fy.baselibrary.base.PopupDismissListner;
import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.dress.DressColor;
import com.fy.baselibrary.dress.DressUtils;
import com.fy.baselibrary.utils.DensityUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.ScreenUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 应用 所有dialog 的父类
 * Created by fangs on 2017/3/13.
 */
public abstract class CommonDialog extends DialogFragment {
    /**
     * 统一 弹窗宽度占屏幕百分比
     */
    public static final int WidthPercent = 80;

    private static final String WIDTHPERCENT = "widthPercent";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String DIM = "dim_amount";
    private static final String GRAVITY = "show_gravity";
    private static final String CANCEL = "out_cancel";
    private static final String IS_KEY_BACK = "KEY_BACK";
    private static final String ANIM = "anim_style";
    private static final String LAYOUT = "layout_id";

    protected View mRootView;

    PopupDismissListner dialogList;

    /** dialog显示位置 */
    protected int gravity = Gravity.CENTER;
    /** dialog进出动画 资源ID */
    protected int anim = android.R.style.Animation_Translucent;
    @LayoutRes
    protected int layoutId;

    /** 是否 拦截物理返回键  [true 拦截，点击返回键 无效]*/
    protected boolean isKeyBack = false;
    /** 点击window外的区域 是否消失 */
    protected boolean isHide = false;
    /** 灰度深浅 */
    protected float dimAmount = 0.5f;

    /** 宽度 -1(ViewGroup.LayoutParams.MATCH_PARENT)：撑满；-2(ViewGroup.LayoutParams.WRAP_CONTENT)：自适应； 其他固定数值 */
    protected int width = -2;
    /** 高度 -1：撑满 -2：自适应 其他固定数值 */
    protected int height = -2;
    /** 宽度 百分比（如：屏幕宽度 的 50%）*/
    protected int widthPercent = -1;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /** 设置dialog 布局 */
    protected abstract int initLayoutId();

    /** 渲染数据到View中 */
    public abstract void convertView(ViewHolder holder, CommonDialog dialog);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.commonDialog);
        layoutId = initLayoutId();

        //恢复保存的数据
        if (null != savedInstanceState) {
            widthPercent = savedInstanceState.getInt(WIDTHPERCENT);
            width = savedInstanceState.getInt(WIDTH);
            height = savedInstanceState.getInt(HEIGHT);
            dimAmount = savedInstanceState.getFloat(DIM);
            gravity = savedInstanceState.getInt(GRAVITY);
            isHide = savedInstanceState.getBoolean(CANCEL);
            isKeyBack = savedInstanceState.getBoolean(IS_KEY_BACK);
            anim = savedInstanceState.getInt(ANIM);
            layoutId = savedInstanceState.getInt(LAYOUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(layoutId, container, false);

            convertView(ViewHolder.createViewHolder(getContext(), mRootView), this);
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        }

        //使用 colorMatrix
//        DressColor dressColor = DressUtils.getDressColor(getContext());
//        if (null != dressColor){
//            ColorMatrix cm = dressColor.getColorMatrix();
//            Paint rootPaint = new Paint();
//            rootPaint.setColorFilter(new ColorMatrixColorFilter(cm));
//            mRootView.setLayerType(View.LAYER_TYPE_HARDWARE, rootPaint);
//        }

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();

        if (!isKeyBack)setOnKeyListener();
    }

    /**
     * 初始化 dialog 样式
     */
    protected void initParams() {
        Window window = getDialog().getWindow();
        if (null != window) {
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度或高度充满整个屏幕
            WindowManager.LayoutParams params = window.getAttributes();

            if (widthPercent > 0){
                params.width = ScreenUtils.getScreenWidth() * widthPercent / 100;
            } else {
                params.width = width > 0 ? DensityUtils.dp2px(width) : width;
            }
            params.height = height > 0 ? DensityUtils.dp2px(height) : height;

            params.dimAmount = dimAmount;//调节灰色背景透明度[0-1]，默认0.5f
            window.setGravity(gravity);  //设置dialog显示的位置
            window.setWindowAnimations(anim);  //添加动画

            window.setAttributes(params);
        }

        setCancelable(isHide);
    }

    /**
     * 屏幕旋转等导致DialogFragment销毁后重建时保存数据
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(WIDTHPERCENT, widthPercent);
        outState.putInt(WIDTH, width);
        outState.putInt(HEIGHT, height);
        outState.putFloat(DIM, dimAmount);
        outState.putInt(GRAVITY, gravity);
        outState.putBoolean(CANCEL, isHide);
        outState.putBoolean(IS_KEY_BACK, isKeyBack);
        outState.putInt(ANIM, anim);
        outState.putInt(LAYOUT, layoutId);
    }


    /**
     * 设置 dialog显示位置
     * @param gravity
     */
    public CommonDialog setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 设置 dialog进出动画
     * @param anim
     */
    public CommonDialog setAnim(int anim) {
        this.anim = anim;
        return this;
    }

    /**
     * 设置 是否拦截返回按钮
     * @param keyBack
     * @return
     */
    public CommonDialog setKeyBack(boolean keyBack) {
        this.isKeyBack = keyBack;
        return this;
    }

    /**
     * 点击window外的区域 是否消失
     * @param hide
     */
    public CommonDialog setHide(boolean hide) {
        isHide = hide;
        return this;
    }

    /**
     * 设置 弹窗宽度占屏幕百分比
     * @param widthPercent
     * @return
     */
    public CommonDialog setWidthPercent(int widthPercent) {
        this.widthPercent = widthPercent;
        return this;
    }

    /**
     * 设置 宽度值
     * @param width
     */
    public CommonDialog setWidthPixels(int width) {
        this.width = width;
        return this;
    }

    /**
     * 设置 高度值
     * @param height
     */
    public CommonDialog setHeightPixels(int height) {
        this.height = height;
        return this;
    }

    /**
     * 设置 灰色背景透明度 ([0-1]，默认0.5f)
     * @param dimAmount
     * @return
     */
    public CommonDialog setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    /**
     * 设置 dialog 关闭监听
     * @param dialogList
     */
    public CommonDialog setDialogList(PopupDismissListner dialogList) {
        this.dialogList = dialogList;
        return this;
    }

    //重写 fragmentDialog show 方法
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            Class c=Class.forName("android.support.v4.app.DialogFragment");
            Constructor con = c.getConstructor();
            Object obj = con.newInstance();
            Field dismissed = c.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(obj,false);
            Field shownByMe = c.getDeclaredField("mShownByMe");
            shownByMe.setAccessible(true);
            shownByMe.set(obj,false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = manager.beginTransaction();
        if (this.isAdded()) {
            ft.remove(this).commitAllowingStateLoss();
        }

        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 关闭对话框
     * @param isDismiss 是否拦截关闭对话框 命令
     */
    public void dismiss(boolean isDismiss) {
        if (isDismiss) return;

        super.dismiss();
        if (null != dialogList)dialogList.onDismiss();
    }


    protected void setOnKeyListener() {
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                L.v("dialog onkey", "按下返回键");
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss(false);
                    return true;
                }
                return false;
            }
        });
    }
}
