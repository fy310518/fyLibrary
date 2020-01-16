package com.fy.baselibrary.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 基本的加载动画 抽象类 ---》衍生出 刷新、加载更多 动画
 * Created by fangs on 2018/9/26 18:10.
 */
public abstract class RefreshAnimView extends FrameLayout{

    public RefreshAnimView(Context context) {
        super(context);
    }

    public RefreshAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 动画默认样式
     */
    public abstract void idle();

    /**
     * 拉动中（准备刷新）
     */
    public abstract void ready();

    /**
     * 动画执行中
     */
    public abstract void triggered();

}
