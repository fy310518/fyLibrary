package com.fy.baselibrary.rv.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.fy.baselibrary.aop.annotation.ClickFilter;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.rv.utils.WrapperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 通用的Adapter
 * Created by fangs on 2017/7/31.
 */
public abstract class RvCommonAdapter<Item> extends RecyclerView.Adapter<ViewHolder> implements Filterable, View.OnClickListener{
    private final static int TYPE_HEAD = 100000;
    private final static int TYPE_FOOTER = 200000;

    private static final int TYPE_EMPTY = -2;// 空布局的ViewType
    // 是否显示空布局，默认不显示
    private boolean showEmptyView = false;

    protected Context mContext;
    protected int mLayoutId;
    protected List<Item> mDatas;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    protected SparseBooleanArray mSelectedPositions;//保存多选 数据
    protected RecyclerView mRv;
    protected int mSelectedPos = -1;//实现单选  保存当前选中的position

    protected OnListener.OnEmptyClickListener OnEmptyClickListener;//列表条目点击事件
    protected OnListener.OnitemClickListener itemClickListner;//列表条目点击事件
    protected OnListener.OnRemoveItemListener removeItemListener;
    public OnListener.OnChangeItemListener changeItemListener;

    public RvCommonAdapter(Context context, int layoutId, List<Item> datas) {
        init(context, layoutId, datas);
    }

    public RvCommonAdapter(Context context, int layoutId, List<Item> datas, RecyclerView rv) {
        init(context, layoutId, datas);
        this.mRv = rv;
    }

    private void init(Context context, int layoutId, List<Item> datas) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mDatas = datas;

        this.mSelectedPositions = new SparseBooleanArray();
    }

    @Override
    public int getItemCount() {
        int count = getHeadersCount() + getFootersCount() + getRealItemCount();
        if (showEmptyView && getRealItemCount() == 0 ) count++;//增加一个 item，显示 空布局

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        } else if (isShowEmpty(position)){
            return TYPE_EMPTY;
        } else {
            return super.getItemViewType(position - getHeadersCount());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (null != mHeaderViews.get(viewType)) {//头
            return ViewHolder.createViewHolder(parent.getContext(), mHeaderViews.get(viewType));
        } else if (null != mFootViews.get(viewType)) {//尾
            return ViewHolder.createViewHolder(parent.getContext(), mFootViews.get(viewType));
        } else {
            ViewHolder viewHolder;
            if (viewType == TYPE_EMPTY) {//空布局
                viewHolder = ViewHolder.createViewHolder(mContext, parent, ConfigUtils.getOnStatusAdapter().emptyDataView());
                viewHolder.itemView.setOnClickListener(view -> {
                    if (null != OnEmptyClickListener) OnEmptyClickListener.onRetry();
                });
            } else {//主体
                viewHolder = ViewHolder.createViewHolder(mContext, parent, mLayoutId);
                bindOnClick(viewHolder);
            }
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (isHeaderViewPos(position) || isFooterViewPos(position) || isShowEmpty(position)) {
            return;
        }

        int centerPosition = position - getHeadersCount();//计算 主体数据 position
        convert(holder, mDatas.get(centerPosition), centerPosition);

//        设置 tag 对应 onCreateViewHolder() 设置点击事件
        holder.itemView.setTag(mDatas.get(centerPosition));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                } else if (mFootViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                }

                if (oldLookup != null) return oldLookup.getSpanSize(position);

                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
        }
    }


    /**
     * 渲染数据到 View中
     *
     * @param holder
     * @param item
     */
    public abstract void convert(ViewHolder holder, Item item, int position);

    /**
     * 绑定点击事件
     *
     * @param viewHolder
     */
    protected void bindOnClick(ViewHolder viewHolder) {
//        避免 在onBindViewHolder里面频繁创建事件回调，应该在 onCreateViewHolder()中每次为新建的 View 设置一次即可
        if (null != itemClickListner) {
//            需要在 convert() 最后使用 holder.itemView.setTag(Item)
            viewHolder.itemView.setOnClickListener(this);
        }
    }

    @ClickFilter()
    @Override
    public void onClick(View v) {
        itemClickListner.onItemClick(v);
    }


    public List<Item> getmDatas() {
        return this.mDatas;
    }

    public void setmDatas(List<Item> list) {
        mDatas.clear();
        mDatas.addAll(list);
    }

    /**
     * 添加data，从指定location中加入
     *
     * @param location
     * @param item
     */
    public void addData(int location, Item item) {
        this.mDatas.add(location, item);
    }

    /**
     * 追加一个集合
     *
     * @param data
     */
    public void addData(List<Item> data) {
        this.mDatas.addAll(data);
    }

    /**
     * 追加一个集合
     *
     * @param location
     * @param data
     */
    public void addData(int location, List<Item> data) {
        this.mDatas.addAll(location, data);
    }

    /**
     * 删除指定 Location 位置的data
     *
     * @param location
     */
    public void removeData(int location) {
        if (location < getItemCount()) this.mDatas.remove(location - getHeadersCount());
    }

    /**
     * 从列表中移除指定 collection 中包含的所有元素
     * @param data
     */
    public void removeData(List<Item> data) {
        this.mDatas.removeAll(data);
    }

    /**
     * 清理 多选状态
     */
    public void cleanChecked() {
        mSelectedPositions.clear();
    }

    /**
     * 设置给定位置条目的选择状态
     *
     * @param position
     * @param isChecked
     */
    public void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }

    /**
     * 根据位置判断条目是否选中
     *
     * @param position
     * @return
     */
    public boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    /**
     * 设置全选 or 反选
     *
     * @param isAllSelect
     */
    public void setIsAllSelect(boolean isAllSelect) {
        for (int i = 0; i < getItemCount(); i++) {
            setItemChecked(i, isAllSelect);
        }
    }

    /**
     * 获取 是否 全选
     * @return
     */
    public boolean getIsAllSelect(){
        if (mSelectedPositions.size() < getRealItemCount()) return false;

        for (int i = 0; i < mSelectedPositions.size(); i++) {
            if (!mSelectedPositions.valueAt(i)) return false;
        }

        return true;
    }

    public SparseBooleanArray getmSelectedPositions() {
        return mSelectedPositions;
    }


    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    //判断是否显示空布局
    public boolean isShowEmpty(int position) {
        return showEmptyView && getRealItemCount() == 0 && getHeadersCount() - position == 0;
    }

    //设置是否显示空布局
    public void setShowEmptyView(boolean showEmptyView){
        this.showEmptyView = showEmptyView;
    }

    /**
     * 定义 添加 头部布局 方法
     * @param view
     */
    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + TYPE_HEAD, view);
    }

    /**
     * 定义 添加 底部布局 方法
     * @param view
     */
    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + TYPE_FOOTER, view);
    }

    /** 清理头部布局 */
    public void cleanHeader(){
        mHeaderViews.clear();
    }

    /** 清理 底部布局 */
    public void cleanFoot(){
        mFootViews.clear();
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }

    /**
     * 主体的 数据数量
     * @return
     */
    private int getRealItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }


    /**
     * 设置空布局 点击事件 回调接口
     */
    public void setOnEmptyClickListener(OnListener.OnEmptyClickListener onEmptyClickListener) {
        OnEmptyClickListener = onEmptyClickListener;
    }

    /**
     * 设置 item 点击事件 监听
     *
     * @param itemClickListner
     */
    public void setItemClickListner(OnListener.OnitemClickListener itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    /**
     * 设置 item 删除事件 监听
     *
     * @param removeItemListener
     */
    public void setRemoveItemListener(OnListener.OnRemoveItemListener removeItemListener) {
        this.removeItemListener = removeItemListener;
    }

    /**
     * 设置 item 更新事件 监听
     *
     * @param changeItemListener
     */
    public void setChangeItemListener(OnListener.OnChangeItemListener changeItemListener) {
        this.changeItemListener = changeItemListener;
    }

    public void setmRv(RecyclerView mRv) {
        this.mRv = mRv;
    }

    /**
     * 获取 单选 的 条目位置
     * @return
     */
    public int getSelectedPos(){
        return mSelectedPos;
    }

    public void setmSelectedPos(int mSelectedPos) {
        this.mSelectedPos = mSelectedPos;
    }

//    单选样板代码
//    holder.setOnClickListener(R.id.llHostRoot, new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            //实现单选方法三： RecyclerView另一种定向刷新方法：不会有白光一闪动画 也不会重复onBindVIewHolder
//            ViewHolder couponVH = (ViewHolder) mRv.findViewHolderForLayoutPosition(mSelectedPos);
//            if (couponVH != null) {//还在屏幕里
//                setSelectedImg(couponVH, false);//这个方法是 子adapter 定义的，根据选中状态设置不同的样式
//            } else {//add by 2016 11 22 for 一些极端情况，holder被缓存在Recycler的cacheView里，
//                //此时拿不到ViewHolder，但是也不会回调onBindViewHolder方法。所以add一个异常处理
//                if (mSelectedPos > -1)notifyItemChanged(mSelectedPos);
//            }
//            if (mSelectedPos > -1) mDatas.get(mSelectedPos).setSelected(false);//不管在不在屏幕里 都需要改变数据
//            //设置新Item的勾选状态
//            mSelectedPos = position;
//            if (mSelectedPos > -1) mDatas.get(mSelectedPos).setSelected(true);
//            setSelectedImg(holder, true);
//
//            if (null != onClickListener) onClickListener.onItemClick();
//        }
//    });


    //过滤器上的锁可以同步复制原始数据。
    private final Object mLock = new Object();
    //对象数组的备份，当调用 Filter 的时候初始化和使用。此时，对象数组只包含已经过滤的数据。
    private ArrayList<Item> mOriginalValues;

    public ArrayList<Item> getmOriginalValues() {
        if (mOriginalValues == null) {
            return new ArrayList<>();
        }
        return mOriginalValues;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override//执行过滤操作
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();//过滤的结果
                //原始数据备份为空时，上锁，同步复制原始数据
                if (mOriginalValues == null) {
                    synchronized (mLock) {
                        mOriginalValues = new ArrayList<>(mDatas);
                    }
                }

                //过滤条件为空
                if (constraint == null || constraint.length() == 0) {
                    ArrayList<Item> list;
                    synchronized (mLock) {//同步复制一个原始备份数据
                        list = new ArrayList<>(mOriginalValues);
                    }
                    results.values = list;
                    results.count = list.size();//此时返回的results就是原始的数据，不进行过滤
                } else {
                    ArrayList<Item> values;
                    synchronized (mLock) {//同步复制一个原始备份数据
                        values = new ArrayList<>(mOriginalValues);
                    }

                    int count = values.size();
                    ArrayList<Item> newValues = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        Item value = values.get(i);//从List<Item>中拿到 对象
                        if (filterRule(value, constraint)) {
                            newValues.add(value);//将这个item加入到数组对象中
                        }
                    }

                    results.values = newValues;//此时的results就是过滤后的List<Item>数组
                    results.count = newValues.size();
                }
                return results;
            }

            @Override//把过滤后的值返回出来
            protected void publishResults(CharSequence constraint, FilterResults results) {
                setmDatas((List<Item>) results.values);//此时，Adapter数据源就是过滤后的Results
                notifyDataSetChanged();//这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
            }
        };
    }

    /**
     * 如需 根据关键字筛选功能，子类 adapter 重写此方法，定义过滤规则
     * @param value         bean
     * @param constraint    过滤条件
     * @return              满足过滤条件返回 true
     */
    public boolean filterRule(Item value, CharSequence constraint){
        return false;
    }
}
