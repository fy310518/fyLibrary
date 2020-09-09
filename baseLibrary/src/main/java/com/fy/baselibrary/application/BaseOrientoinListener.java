package com.fy.baselibrary.application;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

/**
 * 监听系统 屏幕方向
 * Created by fangs on 2017/11/16.
 */
public class BaseOrientoinListener extends OrientationEventListener {

//     android:screenOrientation="sensor" 此属性表示 屏幕根据物理传感器方向转动
//     android:configChanges="keyboardHidden|orientation|screenSize" 添加此属性表示横竖屏切换activity不重启
//     android:configChanges="uiMode" 监听深色主题是否开启
//     Application(很少用) activity 或者 fragment 重写 onConfigurationChanged() 以【监听屏幕旋转 或者 监听到暗黑的主题是否开启】 如下：

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            //横屏
//        } else {
//            //竖屏
//        }
//
//        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        switch (currentNightMode) {
//            case Configuration.UI_MODE_NIGHT_NO:
//                // 关闭
//                break;
//            case Configuration.UI_MODE_NIGHT_YES:
//                // 开启
//                break;
//    }

    public static final String TAG = "activity";
    private Activity context;

    public BaseOrientoinListener(Activity context) {
        super(context);
        this.context = context;
    }

    public BaseOrientoinListener(Context context, int rate) {
        super(context, rate);
    }

    @Override
    public void onOrientationChanged(int orientation) {
//        L.d(TAG, "orention" + orientation);
        int screenOrientation = context.getResources().getConfiguration().orientation;
        if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else if (orientation > 225 && orientation < 315) { //设置横屏
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        } else if (orientation > 135 && orientation < 225) {
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }
        }
    }
}
