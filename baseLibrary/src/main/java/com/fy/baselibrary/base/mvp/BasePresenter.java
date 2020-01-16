package com.fy.baselibrary.base.mvp;

/**
 * describe： 定义 MVP Presenter 抽象超类;
 *            持有视图层和控制层对象引用
 * Created by fangs on 2019/1/22 17:26.
 */
public abstract class BasePresenter<T extends IView> implements IPresenter<T> {

    protected T mView;

    @Override
    public void attachView(T view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }
}
