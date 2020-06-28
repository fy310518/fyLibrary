package com.fy.baselibrary.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.statuslayout.LoadSirUtils;
import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.statuslayout.StatusLayoutManager;
import com.fy.baselibrary.utils.JumpUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.cache.ACache;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment 基类
 * Created by fangs on 2017/4/26.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener, OnSetStatusView {
    public final String TAG = "lifeCycle --> " + getClass().getSimpleName();

    protected ACache mCache;

    protected AppCompatActivity mContext;
    protected StatusLayoutManager slManager;

    protected View mRootView;
    protected Unbinder unbinder;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /**
     * 设置 fragment 视图
     * @return
     */
    protected abstract int setContentLayout();
    /** 初始化 */
    protected abstract void baseInit();

    /** 设置懒加载 */
    protected void lazyData() {}


    @Override
    public void onClick(View view) {}

    @Override
    public void onRetry() {}

    @Override
    public View setStatusView(){return mRootView;}

    @Override
    public void showHideViewFlag(int flagId) {
        if (null != slManager) slManager.showHideViewFlag(flagId);
    }

    @Override//Fragment和Activity建立关联的时候调用
    public void onAttach(Context context) {
        super.onAttach(context);
        L.e(TAG, "onAttach()");

        this.mContext = (AppCompatActivity) context;
        mCache = ACache.get(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.e(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mRootView) {
            if (setContentLayout() > 0){
                mRootView = inflater.inflate(setContentLayout(), container, false);
                unbinder = ButterKnife.bind(this, mRootView);
            }

            baseInit();
            if (setContentLayout() > 0) slManager = LoadSirUtils.initStatusLayout(this);

            isViewCreated = true;
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        }
        L.e(TAG, "onCreateView()");

        return mRootView;
    }

    @Override//当Activity中的onCreate方法执行完后调用
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.e(TAG, "onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        isActivityShow = true;
        L.e(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        L.e(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityShow = false;
        L.e(TAG, "onStop()");
    }

    @Override//Fragment中的布局被移除时调用
    public void onDestroyView() {
        super.onDestroyView();
        L.e(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.e(TAG, "onDestroy()");

        if (null != unbinder){
            unbinder.unbind();
        }
    }

    @Override//Fragment和Activity解除关联的时候调用
    public void onDetach() {
        super.onDetach();
        L.e(TAG, "onDetach()");
    }

    /**
     * fragment 设置 toolbar
     * @param title
     * tips：重写 onCreateOptionsMenu 方法 可以设置 菜单
     */
    protected void setToolbar(Toolbar toolbar, @StringRes int title){
        setToolbar(toolbar, ResUtils.getStr(title), null);
    }

    protected void setToolbar(Toolbar toolbar, String title, View.OnClickListener listener){
        if (ConfigUtils.isTitleCenter()) {
            toolbar.setTitle("");
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(title);
            toolbarTitle.setTextColor(ResUtils.getColor(ConfigUtils.getTitleColor()));
            toolbarTitle.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(title);
        }

        if (ConfigUtils.getBgColor() > 0)
            toolbar.setBackgroundColor(ResUtils.getColor(ConfigUtils.getBgColor()));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (null != listener){
            //在Toolbar左边显示一个返回按钮
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //替换toolbar 自带的返回按钮
            if (ConfigUtils.getBackImg() > 0) toolbar.setNavigationIcon(ConfigUtils.getBackImg());

            toolbar.setNavigationOnClickListener(listener);
        }

        setHasOptionsMenu(true);
    }


    /**
     * 当前activity 是否显示
     * 目的：解决activity 跳转到一个已存在的activity 并显示指定位置的fragment，onResume方法重走两次问题
     */
    private boolean isActivityShow;
    //activity内部切换 Fragment 不回调onPause() 和 onResume() 方法解决方案
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isActivityShow) return;
        if (hidden) {// 不在最前端界面显示
            onPause();
        } else {// 重新显示到最前端中
            onResume();
        }
    }


    /** Fragment的View加载完毕的标记 */
    private boolean isViewCreated;
    /** Fragment对用户可见的标记 */
    private boolean isUIVisible;
    //当fragment结合viewpager使用的时候 这个方法会调用
    //这个方法是在oncreateView之前使用 不要使用控件
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }

        L.e(TAG, "setUserVisibleHint()");
    }

    // 这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,
    // 必须确保onCreateView加载完毕且页面可见,才加载数据
    private void lazyLoad() {
        if (isViewCreated && isUIVisible) {
            lazyData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;
        }
    }
}
