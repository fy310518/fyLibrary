package com.fy.baselibrary.widget.wheel.simple;

/**
 * description 定义 WheelView 选中项 回调接口
 * Created by fangs on 2020/6/9 14:10.
 */
public interface OnWheelSelectListener<T extends ArrayItem> {

    /**
     * WheelView 选中项 回调
     * @param selectItem
     */
    void onSelect(T selectItem);
}
