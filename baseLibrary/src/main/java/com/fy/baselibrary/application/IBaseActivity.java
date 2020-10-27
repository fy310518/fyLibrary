package com.fy.baselibrary.application;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.fy.baselibrary.base.mvvm.BaseViewModel;

/**
 * activity 实现接口 统一规范
 * 项目自己新建的 activity 建议实现 此 接口
 * Created by fangs on 2018/3/13.
 */
public interface IBaseActivity<VM extends BaseViewModel, VDB extends ViewDataBinding> {

    /**
     * 是否显示 标题栏
     * @return
     */
    boolean isShowHeadView();

    /**
     * 设置Activity 界面布局文件id
     * @return
     */
    int setView();

    /**
     * 初始化
     */
    void initData(Activity activity, Bundle savedInstanceState);

}
