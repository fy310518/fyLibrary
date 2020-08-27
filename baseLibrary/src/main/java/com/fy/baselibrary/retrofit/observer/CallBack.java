package com.fy.baselibrary.retrofit.observer;

import com.fy.baselibrary.utils.TransfmtUtils;

/**
 * 网络请求 回调 观察者 【带 文件上传、下载 进度】(增强 RequestBaseObserver)
 * Created by fangs on 2018/5/21.
 */
public abstract class CallBack<T> extends RequestBaseObserver<T> {

    public CallBack() {}

    public CallBack(IProgressDialog pDialog) {
        super(pDialog);
    }

    public CallBack(Object context) {
        super(context);
    }

    @Override
    public void onNext(T t) {
        if (t instanceof Double) {
            String percent = TransfmtUtils.doubleToKeepTwoDecimalPlaces(((Double) t).doubleValue());
            onProgress(percent);
        } else {
            super.onNext(t);
        }
    }
}
