package com.fy.baselibrary.dress.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * description 清理因为添加 颜色矩阵（本质是使用了 硬件加速）引起 UI异常【如：地图，自定义相机功能的 页面】
 * Created by fangs on 2020/9/11 10:12.
 */
public class DressCleanFrameLayout extends FrameLayout {

    public DressCleanFrameLayout(Context context) {
        super(context);
    }

    public DressCleanFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 判断 ViewGroup 中是否存在，指定的view
     * @param view
     * @param targetViewName
     */
    public static boolean getChildA(View view, String targetViewName) {
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                if(viewChild.getClass().getName().equals(targetViewName)) return true;

                if (getChildA(viewChild, targetViewName)) return true;
            }
        }
        return false;
    }
}
