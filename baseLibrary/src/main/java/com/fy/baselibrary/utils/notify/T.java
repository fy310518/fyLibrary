package com.fy.baselibrary.utils.notify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.ResUtils;

/**
 * Toast统一管理类 (解决多次弹出toast)
 * Created by fangs on 2017/3/1.
 */
public class T {

    /**
     * 显示toast 开关
     */
    public static boolean isShow = true;
    private static Toast toast;

    private T() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        show(message.toString(), Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(int message) {
        show(ResUtils.getStr(message), Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        show(message.toString(), Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(int message) {
        show(ResUtils.getStr(message), Toast.LENGTH_LONG);
    }

    /**
     * 显示系统 toast
     *
     * @param message 消息
     */
    @SuppressLint("ShowToast")
    private static void show(String message, int duration) {
        if (isShow) {
            if (null == toast) {
                toast = Toast.makeText(ConfigUtils.getAppCtx(), "", duration);
            } else {
                toast.cancel();
                toast = Toast.makeText(ConfigUtils.getAppCtx(), "", duration);
            }
            NotificationManagerCompat.from(ConfigUtils.getAppCtx()).areNotificationsEnabled();
            toast.setText(message);
            toast.show();
        }
    }


    /**
     * 自定义 布局的Toast
     *
     * @param duration
     * @param message
     * @param tLayouId  自定义布局
     */
    public static void showQulifier(int duration, CharSequence message, @LayoutRes int tLayouId) {
        if (null == toast) {
            LayoutInflater inflate = (LayoutInflater) ConfigUtils.getAppCtx().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflate.inflate(tLayouId, null);

            toast = new Toast(ConfigUtils.getAppCtx());
            toast.setView(view);
            toast.setDuration(duration);
        } else {
            toast.setText(message);
        }

        show(message.toString(), Toast.LENGTH_LONG);
    }
}
