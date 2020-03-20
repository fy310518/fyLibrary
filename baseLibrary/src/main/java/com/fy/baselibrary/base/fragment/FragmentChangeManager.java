package com.gcstorage.chat;

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

    private Fragment mCurrentFrgment;//当前显示的fragment
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

            setCommitTransaction(null, showFragment, position, false, 0);
        }
    }

    /**
     * fragment 懒加载 (界面切换控制)
     *
     * @param position
     */
    public void setFragments(int position, boolean isRemove, int count) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        setFragmentTransition(fragmentTransaction, currentIndex, position);

        Fragment showFragment = mFragments.get(position);

        setCommitTransaction(fragmentTransaction, showFragment, position, isRemove, count);
    }

    /**
     * fragment 回退 , 解决 fragment重影
     * @param fragmentTransaction
     * @param showFragment
     * @param position
     * @param isRemove   是否 移除最后 count 个 fragment
     */
    private void setCommitTransaction(FragmentTransaction fragmentTransaction, Fragment showFragment, int position, boolean isRemove, int count) {
        if (null == fragmentTransaction) fragmentTransaction = mFragmentManager.beginTransaction();

        //判断当前的Fragment是否为空，不为空则隐藏
        if (null != mCurrentFrgment) {
            fragmentTransaction.hide(mCurrentFrgment);
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

        if (isRemove){
            for (int i = 0; i < count; i++) {
                fragmentTransaction.remove(mFragments.get(mFragments.size() - 1));
                mFragments.remove(currentIndex--);
            }
        }

        //保存当前显示的那个Fragment
        mCurrentFrgment = showFragment;
        currentIndex = position;
        fragmentTransaction.commitAllowingStateLoss();
    }



    /**
     * 设置fragment 转场动画
     *
     * @param fragmentTransaction 事物
     * @param currentIndex        当前fragment 在事物中的下标
     * @param position            将要显示的fragment的下标
     */
    @SuppressLint("ResourceType")
    public void setFragmentTransition(FragmentTransaction fragmentTransaction,
                                      int currentIndex, int position) {

        if (styleResAnim > 0){
            fragmentTransaction.setTransitionStyle(styleResAnim);

        } else if (inEnter > 0 && inExit > 0 && outEnter > 0 && outExit > 0) {
            //设置自定义过场动画
            if (currentIndex > position) {
                fragmentTransaction.setCustomAnimations(
                        outEnter,
                        outExit);
            } else if (currentIndex < position) {
                fragmentTransaction.setCustomAnimations(
                        inEnter,
                        inExit);
            }
        }

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

    /**
     * 向fragment 管理器 添加一个 fragment 并显示
     * @param fragment
     */
    public void addFragment(Fragment fragment){
        mFragments.add(fragment);
        setFragments(mFragments.size() - 1, false, 0);
    }

    /**
     * fragment 回退 删除最后一个 fragment
     * https://blog.csdn.net/qq_16247851/article/details/52793061
     */
    public void popLastFragment(){
        mFragmentManager.popBackStack(null, 0);
        mFragments.remove(currentIndex--);

        setFragments(currentIndex, false, 0);
    }

    public int getCurrentTab() {
        return currentIndex;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFrgment;
    }

    public int getFmCount(){
        return mFragments.size();
    }


}
