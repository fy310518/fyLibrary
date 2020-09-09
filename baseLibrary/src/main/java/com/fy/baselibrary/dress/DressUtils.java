package com.fy.baselibrary.dress;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

/**
 * description 【https://www.yuque.com/lenebf/fl1svo/sagoxt】
 * Created by fangs on 2020/9/9 11:36.
 */
public class DressUtils {

    private static DressColor dressColor;
    private static boolean isModify;//应用允许中 是否修改了 模式

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

        if (null == dressColor) {
            window.getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
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
