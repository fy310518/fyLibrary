package com.fy.baselibrary.utils;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.fy.baselibrary.application.ioc.ConfigUtils;

/**
 * 屏幕相关的工具类
 * Created by fangs on 2017/3/1.
 */
public class ScreenUtils {

    private ScreenUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 得到设备的 屏幕像素密度 比例
     */
    public static float getScreenDensity() {
        Context context = ConfigUtils.getAppCtx();
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 得到设备的 屏幕像素密度 值
     */
    public static int getScreenDensityDpi() {
        Context context = ConfigUtils.getAppCtx();
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * 获取 屏幕尺寸
     *
     * @return
     */
    public static float getScreenSize(){
        Context context = ConfigUtils.getAppCtx();
        float screenSize = 0;

        Point point = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            wm.getDefaultDisplay().getRealSize(point);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            double x = Math.pow(point.x / dm.xdpi, 2);
            double y = Math.pow(point.y / dm.ydpi, 2);

            screenSize = (float) Math.sqrt(x + y);
        }

        return screenSize;
    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        Context context = ConfigUtils.getAppCtx();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        Context context = ConfigUtils.getAppCtx();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得 状态栏 高度
     *
     * @return
     */
    public static int getStatusHeight() {
        Context context = ConfigUtils.getAppCtx();
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获得 导航栏 高度
     *
     * @return
     */
    public static int getNavigationHeight() {
        Context context = ConfigUtils.getAppCtx();
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列，并获取Item宽度
     * @return
     */
    public static int getImageItemWidth() {
        Context context = ConfigUtils.getAppCtx();
        int screenWidth = getScreenWidth();
        int densityDpi = getScreenDensityDpi();

        int cols = screenWidth / densityDpi;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) (2 * getScreenDensity());
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }



    private static float sNoncompatdensity;
    private static float sNoncompatscaledDensity;

    /**
     * 屏幕适配
     * @param context
     */
    public static void setCustomDensity(Context context, int designWidth) {
        final DisplayMetrics appDisplayMetrics = context.getResources().getDisplayMetrics();
        if (sNoncompatdensity == 0){
            sNoncompatdensity = appDisplayMetrics.density;
            sNoncompatscaledDensity = appDisplayMetrics.scaledDensity;

            //添加字体变化的监听
            context.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体改变后,将appScaledDensity重新赋值
                    if (null != newConfig && newConfig.fontScale > 0) {
                        sNoncompatscaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
                    }
                }
                @Override
                public void onLowMemory() {}
            });
        }


        float targetDensity = appDisplayMetrics.widthPixels / designWidth;
        float targetScaledDensity = targetDensity * (sNoncompatscaledDensity / sNoncompatdensity);
        int targetDensityDpi = (int) (160 * targetDensity);

//        最后在这里将修改过后的值赋给系统参数
        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;
    }

}
