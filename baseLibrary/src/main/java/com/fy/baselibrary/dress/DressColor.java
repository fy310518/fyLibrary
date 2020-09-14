package com.fy.baselibrary.dress;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.support.annotation.NonNull;

/**
 * description 定义界面 色彩处理 接口
 * 【核心 ColorMatrix 类】
 * Created by fangs on 2020/9/9 11:31.
 */
public interface DressColor {
    /**
     * 给界面着色
     * @param activity 被着色界面
     */
    void tint(@NonNull Activity activity);

    /**
     * 界面清除着色
     * @param activity 被清除界面
     */
    void clear(@NonNull Activity activity);

    /**
     * 定义 颜色矩阵
     */
    ColorMatrix getColorMatrix();
}
