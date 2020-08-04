package com.fy.baselibrary.retrofit.observer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.fy.baselibrary.base.dialog.CommonDialog;
import com.fy.baselibrary.widget.refresh.EasyPullLayout;

/**
 * 自定义对话框的dialog
 * Created by fangs on 2017/11/7.
 */
public interface IProgressDialog {

    Context mContext = null;

    /** 传递进来的 环境（AppCompatActivity or v4.app.Fragment） */
    Object obj = null;
    CommonDialog dialog = null;

    EasyPullLayout epl = null;


    /**
     * 显示对话框
     */
    void show();

//    public void show() {
//        if (null != obj){
//            if (obj instanceof AppCompatActivity) {
//                AppCompatActivity activity = (AppCompatActivity) obj;
//                mContext = activity;
//                if (null != dialog) dialog.show(activity.getSupportFragmentManager(), "");
//            } else if (obj instanceof Fragment) {
//                Fragment fragment = (Fragment) obj;
//                mContext = fragment.getContext();
//                if (null != dialog) dialog.show(fragment.getFragmentManager(), "");
//            } else {
//                throw new IllegalArgumentException("The Context must be is AppCompatActivity or v4.app.Fragment.");
//            }
//        }
//    }

    /**
     * 关闭对话框
     */
    void close();
//    public void close() {
//        if (null != dialog && null != mContext) {
//            dialog.dismiss(false);
//        }  else if (null != epl && null != mContext){
//            epl.stop();
//        }
//    }

    /**
     * 关闭对话框获取对话框
     */
    CommonDialog getDialog() ;
//    public CommonDialog getDialog() {
//        return dialog;
//    }

}
