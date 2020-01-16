package com.fy.baselibrary.widget.refresh;

/**
 * 手势拉动 回调接口
 * Created by fangs on 2018/9/27 11:13.
 */
public interface OnPullListenerAdapter {
    /**
     * 手势监听 回调
     */
    void onPull(int type, float fraction, boolean changed);

    /**
     * 根据 type 判断执行刷新请求 or 加载更多请求
     */
    void onTriggered(int type);

    /**
     * 动画还原
     */
    void onRollBack(int type);
}
