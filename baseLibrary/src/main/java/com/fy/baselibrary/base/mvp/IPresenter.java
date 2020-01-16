package com.fy.baselibrary.base.mvp;

/**
 * describe： 定义 mvp presenter 接口
 * Created by fangs on 2019/1/22 17:17.
 */
public interface IPresenter<T extends IView> {

    /**
     * 依附生命view
     * @param view 视图对象
     */
    void attachView(T view);

    /**
     * 分离View
     */
    void detachView();
}
