package com.fy.baselibrary.retrofit.load;

import com.fy.baselibrary.retrofit.observer.RequestBaseObserver;
import com.fy.baselibrary.utils.TransfmtUtils;

/**
 * 自定义文件上传、下载 观察者 (增强 RequestBaseObserver)
 * Created by fangs on 2018/5/21.
 */
public abstract class LoadCallBack<T> extends RequestBaseObserver<T> {

    public LoadCallBack() {}

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
