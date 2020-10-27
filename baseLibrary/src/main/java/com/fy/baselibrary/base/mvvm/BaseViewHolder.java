package com.fy.baselibrary.base.mvvm;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * description: dataBinding ViewHolder
 * Created by fangs on 2020/10/12 16:42.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public ViewDataBinding binding;

    public BaseViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
