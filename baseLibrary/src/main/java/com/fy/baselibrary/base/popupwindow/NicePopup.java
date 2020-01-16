package com.fy.baselibrary.base.popupwindow;

import android.support.annotation.LayoutRes;

import com.fy.baselibrary.base.ViewHolder;

/**
 * 完善 没有扩展需求情况下 使用父类创建 PopupWindow 不够优雅的问题
 * Created by fangs on 2018/3/21.
 */
public class NicePopup extends CommonPopupWindow {

    PopupConvertListener convertListener;

    public NicePopup(Builder builder) {
        this.layoutId = builder.layoutId;
        this.convertListener = builder.convertListener;
    }

    @Override
    protected int initLayoutId() {
        return layoutId;
    }

    @Override
    public void convertView(ViewHolder holder) {
        if (null != convertListener) convertListener.convertView(holder, this);
    }


    /**
     * 绘制 Popup UI 回调
     */
    public interface PopupConvertListener {
        void convertView(ViewHolder holder, CommonPopupWindow popupWindow);
    }


    public static class Builder {

        @LayoutRes
        protected int layoutId;
        PopupConvertListener convertListener;

        public Builder setLayoutId(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        public Builder setConvertListener(PopupConvertListener convertListener) {
            this.convertListener = convertListener;
            return this;
        }

        public static Builder init() {
            return new Builder();
        }


        public NicePopup create() {
            return new NicePopup(this);
        }

    }
}
