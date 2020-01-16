package com.fy.baselibrary.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fy.baselibrary.base.fragment.BaseFragment;

/**
 * describe： 定义 MVP 视图层 之 fragment 抽象超类
 * Created by fangs on 2019/1/24 15:38.
 */
public abstract class BaseMVPFragment<T extends IPresenter> extends BaseFragment implements IView{

    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
    }

    protected void initPresenter() {
        mPresenter = createPresenter();
        //绑定生命周期
        if (null != mPresenter) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void onDestroy() {
        if (null != mPresenter) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

    /**
     * 创建一个 Presenter
     * @return Presenter
     */
    protected abstract T createPresenter();
}
