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

    /** 是否跟随系统 key */
    public static final String isToFollowSystem = "isToFollowSystem";
    /** 当前模式 日间/夜间/护眼 key 保存的是 int类型数据【0：正常模式；1：夜间模式；2：护眼模式；3：灰阶（黑白）模式】*/
    public static final String isNightMode = "isNightMode";
    /** 上次的模式 key【true 深色，false 同上】 */
    public static final String lastTimeUIMode = "lastTimeUIMode";
    /** 是否使用 深色模式 key */
    public static final String useNightMode = "useNightMode";


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
        int isNight = SpfAgent.init("").getInt(isNightMode);
        if (isNight == 1) {//当前模式是夜间模式
            //不使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            isNight = 0;
        } else if (isNight == 0){
            //使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            isNight = 1;
        }

        SpfAgent.init().saveInt(isNightMode, isNight).commit(false);
        activity.getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
        activity.recreate(); // 这个是刷新，不然不起作用
    }

    /**
     * 设置 日间/夜间 模式
     */
    public static void setNightMode(){
        int isNight = SpfAgent.init("").getInt(isNightMode);
        if (isNight == 0) {
            //使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (isNight == 1){
            //不使用夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private static DressColor dressColor;
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

        if (null == dressColor) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            dressColor.tint(activity);
        }
    }

    public static DressColor getColor() {
        return dressColor;
    }


    //设置 Ui模式 样式
    public static void setDress(Activity context){
        DressColor dressColor = getDressColor(context);
        DressUtils.tint(context, dressColor);
    }

    public static DressColor getDressColor(Context context){
        boolean useNightMode = SpfAgent.init("").getBoolean(DressUtils.useNightMode);
        if (!useNightMode) return null;

        boolean isToFollowSystem = SpfAgent.init("").getBoolean(DressUtils.isToFollowSystem);
        DressColor dressColor = null;
        if (isToFollowSystem){//是否跟随系统
            dressColor = DressUtils.isDarkTheme(context) ? new NightColor(view -> view instanceof ImageView) : null;//是否深色模式
        } else {
            int isNight = SpfAgent.init("").getInt(DressUtils.isNightMode);//是否深色模式
            if (isNight == 1){
                dressColor = new NightColor(view -> view instanceof ImageView);
            } else if (isNight == 2){
                dressColor = new EyeProtectionColor(0.7f);
            } else if (isNight == 3){
                dressColor = new GrayColor();
            }
        }

        return dressColor;
    }
}
