package com.fy.baselibrary.dress.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * description </p>
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
