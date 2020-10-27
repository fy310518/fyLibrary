package com.fy.baselibrary.base.fragment;

import android.annotation.SuppressLint;
import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

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

    /**
     * Fragment切换数组
     */
    private List<Fragment> mFragments;

    @AnimatorRes
    @AnimRes
    int inEnter, inExit;

    @AnimatorRes
    @AnimRes
    int outEnter, outExit;

    @StyleRes
    int styleResAnim;

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
     *
     * @param positions 下标数组
     */
    public void setLoadFragments(int... positions) {
        for (int position : positions) {
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
        setFragmentTransition(fragmentTransaction, currentIndex, position);

        Fragment showFragment = mFragments.get(position);

        setCommitTransaction(fragmentTransaction, showFragment, position);
    }

    /**
     * fragment 回退 , 解决 fragment重影
     * @param fragmentTransaction
     * @param showFragment
     * @param position
     */
    private void setCommitTransaction(FragmentTransaction fragmentTransaction, Fragment showFragment, int position) {
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
            fragmentTransaction.addToBackStack(fragmentTag);
        } else {
            fragmentTransaction.show(showFragment);
        }

        //保存当前显示的那个Fragment
        mCurrentFragment = showFragment;
        currentIndex = position;
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 设置fragment 转场动画
     *
     * @param transaction 事物
     * @param currentIndex        当前fragment 在事物中的下标
     * @param position            将要显示的fragment的下标
     */
    @SuppressLint("ResourceType")
    public void setFragmentTransition(FragmentTransaction transaction, int currentIndex, int position) {
        if (styleResAnim > 0){
            transaction.setTransitionStyle(styleResAnim);
        } else if (inEnter > 0 && inExit > 0 && outEnter > 0 && outExit > 0) {
            //设置自定义过场动画
            if (currentIndex > position) {
                transaction.setCustomAnimations(outEnter, outExit);
            } else if (currentIndex < position) {
                transaction.setCustomAnimations(inEnter, inExit);
            }
        }
    }

    /**
     * 向fragment 管理器 添加一个 fragment 并显示
     * @param fragment
     */
    public void addFragment(Fragment fragment){
        mFragments.add(fragment);
        setFragments(mFragments.size() - 1);
    }

    /**
     * fragment 回退 删除最后一个 fragment
     * https://blog.csdn.net/qq_16247851/article/details/52793061
     */
    public void popLastFragment(){
        removeFragment(1);
    }

    //移除指定数量的 fragment
    public void removeFragment(int count){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        for (int i = 0; i < count && mFragments.size() > 0; i++) {
            mFragmentManager.popBackStack(null, 0);
            transaction.remove(mFragments.get(mFragments.size() - 1));
            transaction.detach(mFragments.get(mFragments.size() - 1));
            mFragments.remove(mFragments.size() - 1);
            currentIndex--;//每移除一个fragment currentIndex-- 一次【保证移除 多个fragment时候 正常】
        }

        mCurrentFragment = mFragments.get(currentIndex);//mCurrentFragment 重新赋值
        setFragmentTransition(transaction, currentIndex, currentIndex - 1);
        transaction.commit();
    }

    //进入动画
    public void setInAnim(int inEnter, int inExit) {
        this.inEnter = inEnter;
        this.inExit = inExit;
    }

    //返回动画
    public void setOutAnim(int outEnter, int outExit) {
        this.outEnter = outEnter;
        this.outExit = outExit;
    }

    public void setStyleResAnim(int styleResAnim) {
        this.styleResAnim = styleResAnim;
    }

    public int getCurrentTab() {
        return currentIndex;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public int getFmCount(){
        return mFragments.size();
    }

}
