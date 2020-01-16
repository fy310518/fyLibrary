package com.fy.baselibrary.retrofit.observer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.fy.baselibrary.base.dialog.CommonDialog;

/**
 * 自定义对话框的dialog
 * Created by fangs on 2017/11/7.
 */
public abstract class IProgressDialog {

    /** 传递进来的 环境（AppCompatActivity or v4.app.Fragment） */
    protected Object obj;
    protected CommonDialog dialog;
    protected Context mContext;

    /**
     * 创建对话框 子类实现此方法
     * @param msg
     * @return
     */
    public abstract IProgressDialog setDialogMsg(int msg);

    public CommonDialog getDialog() {
        return dialog;
    }

    /**
     * 显示对话框
     */
    public void show() {
        if (null != dialog && null != obj){
            if (obj instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) obj;
                mContext = activity;
                dialog.show(activity.getSupportFragmentManager(), "");
            } else if (obj instanceof Fragment) {
                Fragment fragment = (Fragment) obj;
                mContext = fragment.getContext();
                dialog.show(fragment.getFragmentManager(), "");
            } else {
                throw new IllegalArgumentException("The Context must be is AppCompatActivity or v4.app.Fragment.");
            }
        }
    }

    /**
     * 关闭对话框
     */
    public void close() {
        if (null != dialog && null != mContext) {
            dialog.dismiss(false);
        }
    }
}
