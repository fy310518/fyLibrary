package com.fy.baselibrary.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.cache.SpfAgent;

/**
 * 日间/夜间 模式 工具类
 * Created by fangs on 2018/3/26.
 */
public class NightModeUtils {

    /** 日间/夜间 key */
    public static final String isNightMode = "isNightMode";

    /** APP 当前模式 （日间/夜间）*/
    public static final String appMode = "appModeSwitch";

    private NightModeUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

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

}
