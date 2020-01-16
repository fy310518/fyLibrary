package com.fy.baselibrary.aop.clickfilter;

import android.view.View;

/**
 * 重复点击判断工具类
 * Created by fangs on 2018/8/23 17:54.
 */
public class ClickUtils {

    /**
     * 最近一次点击的时间
     */
    private static long mLastClickTime;
    /**
     * 最近一次点击的控件ID
     */
    private static int mLastClickViewId;

    /**
     * 判断是否 快速点击
     *
     * @param v  点击view
     * @param intervalMillis  时间间隔（毫秒）
     * @return  true:是；false:不是
     */
    public static boolean isFastClick(View v, long intervalMillis) {
        int viewId = v.getId();
        long time = System.currentTimeMillis();

        long timeInterval = Math.abs(time - mLastClickTime);
        if (timeInterval < intervalMillis && viewId == mLastClickViewId) {
            return true;
        } else {
            mLastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }
}
