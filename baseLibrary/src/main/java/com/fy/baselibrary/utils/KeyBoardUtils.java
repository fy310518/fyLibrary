package com.fy.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

/**
 * 打开或关闭软键盘
 * Created by fangs on 2017/3/1.
 */
public class KeyBoardUtils {

    private KeyBoardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 关闭键盘
     */
    public static void closeKeyBoard(Activity activity) {

//		if (activity.getCurrentFocus() != null) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View view = activity.getWindow().peekDecorView();
            activity.getWindow().getDecorView();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        0);
//
//				}
            }
        }
    }

    public static void forceCloseKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(screen.getWindowToken(), 0);
        imm.toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN);
    }


    /**
     * 打开输入法
     */
    public static void openKeyBoard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 打开输入法
     */
    public static void openKeyBoard(Activity activity) {
//		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
//				|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.showSoftInput(activity.getWindow().getDecorView(),0);
        imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 关闭输入法
     */
    public static void closeInput(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    /**
     * 关闭输入法
     */
    public static void closeInput(Activity activity, ViewGroup view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 若返回true，则表示输入法打开
     *
     * @return
     */
    public static boolean isInputType(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

}
