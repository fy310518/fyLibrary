package com.fy.baselibrary.aop.resultfilter;

import android.content.Intent;

/**
 * 定义 带回调结果的 跳转，回调接口
 * Created by fangs on 2017/5/9.
 */
public interface ResultCallBack {
    /**
     * 类似 activity 的 onActivityResult()
     */
    void onActResult(int requestCode, int resultCode, Intent data);
}
