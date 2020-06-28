package com.fy.baselibrary.widget.wheel.simple;

import java.util.List;

/**
 * describe：定义 WheelView 适配器，数据源 实体类 接口
 * Created by fangs on 2020/4/20 0020 下午 15:01.
 */
public interface ArrayItem<T> {

    String getName();

    List<T> getData();
}
