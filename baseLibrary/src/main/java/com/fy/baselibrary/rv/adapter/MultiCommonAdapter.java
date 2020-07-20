package com.fy.baselibrary.rv.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.fy.baselibrary.base.ViewHolder;

import java.util.List;

/**
 * RecyclerView多种ItemViewType 的adapter
 * Created by fangs on 2017/7/31.
 */
public abstract class MultiCommonAdapter<Item> extends RvCommonAdapter<Item> {

    protected MultiTypeSupport<Item> mMultiTypeSupport;

    public MultiCommonAdapter(Context context, List<Item> datas, MultiTypeSupport<Item> multiTypeSupport) {
        super(context, -1, datas);
        mMultiTypeSupport = multiTypeSupport;
    }

    @Override
    public int getItemViewType(int position) {
        int superType = super.getItemViewType(position);
        if (0 == superType){//主体
            return mMultiTypeSupport.getItemViewType(position - getHeadersCount(), mDatas.get(position - getHeadersCount()));
        } else {
            return superType;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        if (null == viewHolder){
            int layoutId = mMultiTypeSupport.getLayoutId(viewType);
            viewHolder = ViewHolder.createViewHolder(mContext, parent, layoutId);

            bindOnClick(viewHolder);
        }

        return viewHolder;
    }

}
