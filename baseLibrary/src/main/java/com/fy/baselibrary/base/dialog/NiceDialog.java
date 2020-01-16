package com.fy.baselibrary.base.dialog;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.fy.baselibrary.base.ViewHolder;

/**
 * 完善 没有扩展需求情况下 使用父类创建dialog 不够优雅的问题
 * Created by fangs on 2018/3/21.
 */
public class NiceDialog extends CommonDialog {
    private static final String LISTENER = "ViewConvertListener";

    private DialogConvertListener dialogConvertListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            dialogConvertListener = savedInstanceState.getParcelable(LISTENER);
        }
    }


    @Override
    protected int initLayoutId() {
        return layoutId;
    }

    @Override
    public void convertView(ViewHolder holder, CommonDialog dialog) {
        if (null != dialogConvertListener) {
            dialogConvertListener.convertView(holder, dialog);
        }
    }

    /**
     * 创建 dialog
     *
     * @return
     */
    public static NiceDialog init() {
        return new NiceDialog();
    }

    public NiceDialog setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public NiceDialog setDialogConvertListener(DialogConvertListener dialogConvertListener) {
        this.dialogConvertListener = dialogConvertListener;
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LISTENER, dialogConvertListener);
    }
}
