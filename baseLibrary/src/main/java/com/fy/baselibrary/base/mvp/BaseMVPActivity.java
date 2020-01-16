package com.fy.baselibrary.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * describe： 定义 MVP 视图层 之 activity 抽象超类
 * Created by fangs on 2019/1/22 17:29.
 */
public abstract class BaseMVPActivity<T extends IPresenter> extends AppCompatActivity implements IView {

    protected T mPresenter;

    /**
     * mvp 初始化 mPresenter
     */
    public void initPresenter() {
        mPresenter = createPresenter();
        //绑定生命周期
        if (null != mPresenter) {
            mPresenter.attachView(this);
        }
    }

    @Override
    protected void onDestroy() {
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
