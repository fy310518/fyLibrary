package com.fy.baselibrary.base.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.fy.baselibrary.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DESCRIPTION：fragment 管理类
 * Created by fangs on 2019/5/5 10:21.
 */
public class FragmentChangeManager {

    private FragmentManager mFragmentManager;
    private int mContainerViewId;

    private Fragment mCurrentFragment;//当前显示的fragment
    private int currentIndex = 0;    //当前显示的fragment的下标

    /** Fragment切换数组 */
    private List<Fragment> mFragments;

    //处理单fragment
    public FragmentChangeManager(FragmentManager fm, int containerViewId, Fragment fragment) {
        this.mFragmentManager = fm;
        this.mContainerViewId = containerViewId;

        mFragments = new ArrayList<>();
        mFragments.add(fragment);
    }

    //处理多fragment
    public FragmentChangeManager(FragmentManager fm, int containerViewId, List<Fragment> fragments) {
        this.mFragmentManager = fm;
        this.mContainerViewId = containerViewId;
        this.mFragments = fragments;
    }

    /**
     * 初始化 把指定位置（mFragments 集合的下标）的fragment添加到 fragment 事物
     * @param positions 下标数组
     */
    public void setLoadFragments(int... positions){
        for (int position : positions){
            Fragment showFragment = mFragments.get(position);

            setCommitTransaction(null, showFragment, position);
        }
    }

    /**
     * fragment 懒加载 (界面切换控制)
     * @param position
     */
    public void setFragments(int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        AnimUtils.setFragmentTransition(fragmentTransaction, currentIndex, position);

        Fragment showFragment = mFragments.get(position);

        setCommitTransaction(fragmentTransaction, showFragment, position);
    }

    /**
     * 解决 fragment重影
     * @param showFragment
     * @param position
     */
    private void setCommitTransaction(FragmentTransaction fragmentTransaction, Fragment showFragment, int position){
        if (null == fragmentTransaction) fragmentTransaction = mFragmentManager.beginTransaction();

        //判断当前的Fragment是否为空，不为空则隐藏
        if (null != mCurrentFragment) {
            fragmentTransaction.hide(mCurrentFragment);
        }

        if (null == showFragment) return;
        //判断此Fragment是否已经添加到FragmentTransaction事物中
        if (!showFragment.isAdded()) {
            String fragmentTag = showFragment.getClass().getSimpleName();
            fragmentTransaction.add(mContainerViewId, showFragment, fragmentTag);
        } else {
            fragmentTransaction.show(showFragment);
        }

        //保存当前显示的那个Fragment
        mCurrentFragment = showFragment;
        currentIndex = position;
        fragmentTransaction.commitAllowingStateLoss();
    }


    public int getCurrentTab() {
        return currentIndex;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
