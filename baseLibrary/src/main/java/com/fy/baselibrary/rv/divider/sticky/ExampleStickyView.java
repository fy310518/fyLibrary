package com.fy.baselibrary.rv.divider.sticky;

import android.view.View;

import com.fy.baselibrary.utils.Constant;

/**
 * Created by cpf on 2018/1/16.
 */
public class ExampleStickyView implements StickyView {

    @Override
    public boolean isStickyView(View view) {
        StickyBean stickyBean = (StickyBean) view.getTag();
        return stickyBean.getItemType() == Constant.StickyType;
    }

    @Override
    public int getStickViewType() {
        return Constant.StickyType;
    }
}
