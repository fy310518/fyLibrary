package com.fy.baselibrary.statuslayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * 辅助 多状态布局 类
 * Created by fangs on 2017/12/17.
 */
public class TargetContext implements Serializable {

    private Context context;
    private ViewGroup parentView;//多状态 管理 父布局
    private View content;//内容布局
    private int childCount;//默认子view 数目

    public TargetContext(Context context, ViewGroup parentView, View content, int childCount) {
        this.context = context;
        this.parentView = parentView;
        this.content = content;
        this.childCount = childCount;
    }

    public Context getContext() {
        return context;
    }

    public View getContent() {
        return content;
    }

    public int getChildCount() {
        return childCount;
    }

    ViewGroup getParentView() {
        return parentView;
    }
}
