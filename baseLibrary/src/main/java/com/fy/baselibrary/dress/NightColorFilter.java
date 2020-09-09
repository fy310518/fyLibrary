package com.fy.baselibrary.dress;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author lenebf@126.com
 * @since 2020/9/5
 */
public interface NightColorFilter {

    /**
     * 是否对该View逆转夜间模式
     * @param view 需要判定的View
     * @return true-逆转该View
     */
    boolean excludeView(@NonNull View view);
}
