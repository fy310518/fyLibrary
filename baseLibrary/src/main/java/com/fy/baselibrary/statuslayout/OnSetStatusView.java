package com.fy.baselibrary.statuslayout;

import android.view.View;

/**
 * 定义 多布局 相关接口
 * Created by fangs on 2017/12/15.
 */
public interface OnSetStatusView {

    /**
     * 设置 多状态视图显示的 区域(内容视图，必须有父视图)
     *
     * @return
     */
    View setStatusView();

    /**
     * 根据 flag 显示/隐藏 对应的状态视图
     *
     * @param flagId
     */
    void showHideViewFlag(int flagId);

    /**
     * 多布局 点击重试
     */
    void onRetry();

}
