package com.fy.baselibrary.rv.adapter;

/**
 * RecyclerView多种ItemViewType 需要实现此接口
 * Created by fangs on 2017/7/31.
 */
public interface MultiTypeSupport<Item> {

    /**
     * 根据itemType返回item布局文件id
     * @param itemType
     * @return
     */
    int getLayoutId(int itemType);

    /**
     * 根据当前的bean返回item type
     * @param position
     * @param t
     * @return
     */
    int getItemViewType(int position, Item t);

}
