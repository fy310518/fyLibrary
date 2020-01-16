package com.fy.baselibrary.statusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.ScreenUtils;

/**
 * 状态栏和导航栏 操作工具类
 * https://github.com/Zackratos/UltimateBar
 * Created by github on 18/3/14.
 */
public class MdStatusBar {

    private MdStatusBar() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static class StatusBuilder{
        /** 状态栏颜色 */
        public int statusColor;
        /** 状态栏透明度 */
        public int statusAlpha;

        /** 是否设置导航栏颜色 */
        boolean applyNav = false;

        /** 导航栏颜色 */
        public int navColor;
        /** 导航栏透明度 */
        public int navAlpha;

        /**
         * 设置状态栏颜色 和 透明度
         * @param statusColor
         * @param statusAlpha  透明度（0 完全透明，255 完全不透明）
         * @param resName      资源名称（加此参数为了在 module中 不能使用colors资源时候的折中办法）
         * @return
         */
        public StatusBuilder setStatusColor(@ColorRes int statusColor, @IntRange(from = 0, to = 255) int statusAlpha, String resName) {

            if (!TextUtils.isEmpty(resName)){
                statusColor = ResUtils.getColorId(resName);
            }

            this.statusColor = statusColor;
            this.statusAlpha = statusAlpha;
            return this;
        }

        /**
         * 设置导航栏颜色 和 透明度
         * @param navColor
         * @param navAlpha 透明度（255 完全透明，0 完全不透明）
         * @param resName  资源名称（加此参数为了在 module中 不能使用colors资源时候的折中办法）
         * @return
         */
        public StatusBuilder setNavColor(@ColorRes int navColor, @IntRange(from = 0, to = 255) int navAlpha, String resName) {
            if (!TextUtils.isEmpty(resName)){
                navColor = ResUtils.getColorId(resName);
            }
            this.navColor = navColor;
            this.navAlpha = navAlpha;
            this.applyNav = true;
            return this;
        }

        /**
         * 单独定义这个方法，作用是配合 setTransparentBar() 灵活控制导航栏【是否】悬浮在界面之上
         * @param applyNav
         * @return
         */
        public StatusBuilder setApplyNav(boolean applyNav) {
            this.applyNav = applyNav;
            return this;
        }

        public static StatusBuilder init() {
            return new StatusBuilder();
        }

        /**
         * 自定义 状态栏和导航栏 的颜色
         * @param act
         */
        public void setColorBar(Activity act){
            int statusc = ContextCompat.getColor(act, this.statusColor);
            int navc = ContextCompat.getColor(act, this.navColor);
            MdStatusBar.setColorBar(act, statusc, this.statusAlpha, this.applyNav, navc, this.navAlpha);
        }

        /**
         * 设置 状态栏和导航栏 的 透明度
         * @param act
         */
        public void setTransparentBar(Activity act){
            int statusc = ContextCompat.getColor(act, this.statusColor);
            int navc = ContextCompat.getColor(act, this.navColor);
            MdStatusBar.setTransparentBar(act, statusc, this.statusAlpha, this.applyNav, navc, this.navAlpha);
        }

        /**
         * DrawerLayout 实现状态栏和导航栏
         * @param act
         */
        public void setColorBarForDrawer(Activity act) {
            int statusc = ContextCompat.getColor(act, this.statusColor);
            int navc = ContextCompat.getColor(act, this.navColor);
            MdStatusBar.setColorBarForDrawer(act, statusc, this.statusAlpha, this.applyNav, navc, this.navAlpha);
        }
    }


    /**
     * 自定义 状态栏和导航栏 的颜色
     *
     * @param statusColor StatusBar color
     * @param statusDepth StatusBar color depth
     * @param applyNav    apply NavigationBar or no
     * @param navColor    NavigationBar color (applyNav == true)
     * @param navDepth    NavigationBar color depth (applyNav = true)
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setColorBar(Activity act, @ColorInt int statusColor, int statusDepth,
                             boolean applyNav, @ColorInt int navColor, int navDepth) {

        int realStatusDepth = limitDepthOrAlpha(statusDepth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
            window.setStatusBarColor(finalStatusColor);//设置状态栏颜色

            if (applyNav) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }

            int realNavDepth = limitDepthOrAlpha(navDepth);
            int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
            window.setNavigationBarColor(finalNavColor);//设置导航栏的颜色

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            decorView.addView(createStatusBarView(act, finalStatusColor));

            if (navigationBarExist(act)) {
                if (applyNav)window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                int realNavDepth = limitDepthOrAlpha(navDepth);
                int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
                decorView.addView(createNavBarView(act, finalNavColor));
            }
            setRootView(act, true);
        }
    }

    /**
     * 设置 状态栏和导航栏 的 透明度
     *
     * @param statusColor StatusBar color
     * @param statusAlpha StatusBar alpha
     * @param applyNav    apply NavigationBar or no
     * @param navColor    NavigationBar color (applyNav == true)
     * @param navAlpha    NavigationBar alpha (applyNav == true)
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setTransparentBar(Activity act, @ColorInt int statusColor, int statusAlpha,
                                   boolean applyNav, @ColorInt int navColor, int navAlpha) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            int finalStatusColor = statusColor == 0 ? Color.TRANSPARENT : calculateColor(statusColor, statusAlpha);
            window.setStatusBarColor(finalStatusColor);

            if (applyNav) {
                option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }

            int finalNavColor = navColor == 0 ? Color.TRANSPARENT : calculateColor(navColor, navAlpha);
            window.setNavigationBarColor(finalNavColor);

            decorView.setSystemUiVisibility(option);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int finalStatusColor = statusColor == 0 ? Color.TRANSPARENT : calculateColor(statusColor, statusAlpha);

            decorView.addView(createStatusBarView(act, finalStatusColor));

            if (navigationBarExist(act)) {
                if (applyNav)window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                int finalNavColor = navColor == 0 ? Color.TRANSPARENT : calculateColor(navColor, navAlpha);
                decorView.addView(createNavBarView(act, finalNavColor));
            }
        }
    }

    /**
     * DrawerLayout 实现状态栏和导航栏
     * 注：必须在布局文件中 DawerLayout 的子view 的【主界面】添加 android:fitsSystemWindows="true"
     * @param statusColor
     * @param statusDepth
     * @param applyNav
     * @param navColor
     * @param navDepth
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setColorBarForDrawer(Activity act, @ColorInt int statusColor, int statusDepth,
                                      boolean applyNav,
                                      @ColorInt int navColor, int navDepth) {
        int realStatusDepth = limitDepthOrAlpha(statusDepth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            window.setStatusBarColor(Color.TRANSPARENT);

            int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
            decorView.addView(createStatusBarView(act, finalStatusColor), 0);

            if (applyNav) option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

            if (navigationBarExist(act)) {
                window.setNavigationBarColor(Color.TRANSPARENT);
                int realNavDepth = limitDepthOrAlpha(navDepth);
                int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
                decorView.addView(createNavBarView(act, finalNavColor), 1);
            }

            decorView.setSystemUiVisibility(option);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
            decorView.addView(createStatusBarView(act, finalStatusColor), 0);

            if (navigationBarExist(act)) {
                if (applyNav) window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                int realNavDepth = limitDepthOrAlpha(navDepth);
                int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
                decorView.addView(createNavBarView(act, finalNavColor), 1);
            }
        }
    }


    /**
     * 隐藏状态栏和导航栏
     * 注：实现这种效果，必须重写 Activity 的 onWindowFocusChanged 方法，在onWindowFocusChanged()中执行
     * @param applyNav apply NavigationBar
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setHideBar(Activity act, boolean applyNav) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = act.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            if (applyNav) {
                option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            decorView.setSystemUiVisibility(option);
        }
    }

    /**
     * 创建一个 状态栏 高度的 view
     * @param context
     * @param color
     * @return
     */
    public static View createStatusBarView(Context context, @ColorInt int color) {
        View statusBarView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.getStatusHeight());
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    /**
     * 创建一个导航栏高度的 view
     * @param context
     * @param color
     * @return
     */
    public static View createNavBarView(Context context, @ColorInt int color) {
        View navBarView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.getNavigationHeight());
        params.gravity = Gravity.BOTTOM;
        navBarView.setLayoutParams(params);
        navBarView.setBackgroundColor(color);
        return navBarView;
    }

    /**
     * 判断导航栏是否存在
     * @param activity
     * @return
     */
    public static boolean navigationBarExist(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }


    public static void setRootView(Activity activity, boolean fit) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fit);
                ((ViewGroup) childView).setClipToPadding(fit);
            }
        }
    }


    /**
     * 颜色 透明度 判断（必须在 0 --- 255之间）
     * @param depthOrAlpha
     * @return
     */
    public static int limitDepthOrAlpha(int depthOrAlpha) {
        return depthOrAlpha < 0 ? 0 : (depthOrAlpha > 255 ? 255 : depthOrAlpha);
    }

    /**
     * 根据颜色值 和 颜色深度（透明度）计算 颜色值
     * @param color
     * @param alpha
     * @return
     */
    @ColorInt
    public static int calculateColor(@ColorInt int color, int alpha) {
        return Color.argb(limitDepthOrAlpha(255 - alpha),
                Color.red(color),
                Color.green(color),
                Color.blue(color));
    }

}
