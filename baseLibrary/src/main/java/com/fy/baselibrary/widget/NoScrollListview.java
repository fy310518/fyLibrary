package com.fy.baselibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义listview 不能滚动
 *
 * Created by fangs on 2016/6/23.
 */
public class NoScrollListview extends ListView {

    public NoScrollListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
