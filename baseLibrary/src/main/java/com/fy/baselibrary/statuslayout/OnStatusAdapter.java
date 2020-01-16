package com.fy.baselibrary.statuslayout;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

/**
 * 定义 多布局 策略接口
 * Created by fangs on 2017/12/15.
 */
public interface OnStatusAdapter {

    /**
     * 请求错误（失败）布局文件ID
     * @return errorRetryViewId
     */
    @LayoutRes
    int errorViewId();

    /**
     * 空数据 布局文件ID
     * @return emptyDataViewId
     */
    @LayoutRes
    int emptyDataView();

    /**
     * 网络错误 布局文件ID
     * @return netWorkErrorViewID
     */
    @LayoutRes
    int netWorkErrorView();

    /**
     * 出现错误时 显示界面的 刷新按钮 view id
     * 注意 空数据 请求错误 网络错误 布局文件  中的 刷新按钮 ID 要保持一致
     * @return view id
     */
    @IdRes
    int retryViewId();


}
