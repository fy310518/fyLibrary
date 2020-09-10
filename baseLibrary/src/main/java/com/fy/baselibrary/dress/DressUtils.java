package com.fy.baselibrary.dress;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.cache.SpfAgent;

/**
 * description 【https://www.yuque.com/lenebf/fl1svo/sagoxt】
 * Created by fangs on 2020/9/9 11:36.
 */
public class DressUtils {

    /**
     * 关于 深色模式跟随系统处理逻辑【个人观点】：
     * 1、在 application onCreate 中 设置 “上次的模式（lastTimeUIMode）” 数据值；
     * 2、在 application onConfigurationChanged 中监听深色主题开关，并更新 “当前模式（isNightMode）” 数据值
     * 3、在 ActivityLifecycleCallbacks onActivityResumed 回调中 更新UI样式 为 “当前模式”
     */
    private static DressColor dressColor;
    private static boolean isModify;//应用运行中 是否修改了 模式

    /** 是否跟随系统 key */
    public static final String isToFollowSystem = "isToFollowSystem";
    /** 当前模式 日间/夜间 key */
    public static final String isNightMode = "isNightMode";
    /** 上次的模式 key【true 深色，false 浅色】 */
    public static final String lastTimeUIMode = "lastTimeUIMode";



    /**
     * 判断当前系统是否 深色主题
     * 【浅色主题 or 深色主题】
     */
    public static boolean isDarkTheme(Context context){
        int flag = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return flag == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 日间/夜间 模式 切换
     * @param activity
     */
    public static void switchNightMode(AppCompatActivity activity){
        boolean isNight = SpfAgent.init("").getBoolean(isNightMode);
        if (isNight) {//当前模式是夜间模式
            //不使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            //使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        SpfAgent.init().saveBoolean(isNightMode, !isNight).commit(false);
        activity.getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
        activity.recreate(); // 这个是刷新，不然不起作用
    }

    /**
     * 设置 日间/夜间 模式
     */
    public static void setNightMode(){
        boolean isNight = SpfAgent.init("").getBoolean(isNightMode);
        if (isNight) {
            //使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            //不使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    //设置 Ui模式 样式
    public static void setDress(Activity activity){
        boolean isToFollowSystem = SpfAgent.init("").getBoolean(DressUtils.isToFollowSystem);
        if (isToFollowSystem){//是否跟随系统
            DressColor dressColor = isDarkTheme(activity) ? new NightColor(view -> view instanceof ImageView) : null;//是否深色模式
            DressUtils.tint(activity, dressColor);
        } else {
            boolean isNight = SpfAgent.init("").getBoolean(isNightMode);//是否深色模式
            DressColor dressColor = isNight ? new NightColor(view -> view instanceof ImageView) : null;
            DressUtils.tint(activity, dressColor);
        }
    }

    /**
     * 设置界面 色彩处理 对象
     * @param color 色彩处理 对象
     */
    public static void tint(Activity activity, @Nullable DressColor color) {
        DressColor oldColor = dressColor;
        dressColor = color;

        if (null != oldColor) oldColor.clear(activity);

        Window window = activity.getWindow();
        if (null == window) return;
        View view = window.getDecorView();
        if (!view.isHardwareAccelerated()) return;

        if (null == dressColor) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            dressColor.tint(activity);
        }
    }

    public static DressColor getColor() {
        return dressColor;
    }

    public static void setIsModify() {
        DressUtils.isModify = true;
    }

    public static boolean isModify() {
        return isModify;
    }
}
