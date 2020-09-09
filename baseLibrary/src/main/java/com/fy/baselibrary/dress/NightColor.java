package com.fy.baselibrary.dress;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * 夜间模式颜色转换，待完善，不建议使用
 * @author lenebf@126.com
 * @since 2020/9/1
 */
public class NightColor implements DressColor {

    private NightColorFilter nightColorFilter;
    private static List<ObservableNightColorFilter> filters = new ArrayList<>();

    public NightColor(NightColorFilter nightColorFilter) {
        this.nightColorFilter = nightColorFilter;
    }

    @Override
    public void tint(@NonNull Activity activity) {
        Window window = activity.getWindow();
        if (null == window) return;

        View view = window.getDecorView();
        Paint rootPaint = new Paint();
        ColorMatrix cm = new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0
        });
        rootPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, rootPaint);
        if (view instanceof ViewGroup && null != nightColorFilter) {
            filters.add(new ObservableNightColorFilter(nightColorFilter, (ViewGroup) view));
        }
    }

    @Override
    public void clear(@NonNull Activity activity) {
        for (ObservableNightColorFilter filter : filters) {
            if (filter != null && filter.match(activity)) {
                filter.destroy(activity);
            }
        }
    }
}
