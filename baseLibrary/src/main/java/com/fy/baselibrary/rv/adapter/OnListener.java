package com.fy.baselibrary.rv.adapter;

import android.view.View;

/**
 * 自定义 RecycclerView 事件回调接口
 * Created by fangs on 2017/11/20.
 */
public interface OnListener {

    /**
     * item点击 回调接口
     */
    interface OnitemClickListener{

        /**
         * 条目点击事件 回调方法
         * @param view 当前点击的条目的实体类
         */
        void onItemClick(View view);
    }

    /**
     * 删除 item 回调接口
     */
    interface OnRemoveItemListener {
        /**
         * 删除指定 position 的条目回调方法
         * @param position
         */
        void onRemove(int position);
    }

    interface OnInsertItemListener {
        void onInsert(int position);
    }

    /**
     * 更新 item 回调接口
     */
    interface OnChangeItemListener {
        /**
         * 更新指定 position 的条目 回调方法
         * @param position
         */
        void onChange(int position);
    }
}
