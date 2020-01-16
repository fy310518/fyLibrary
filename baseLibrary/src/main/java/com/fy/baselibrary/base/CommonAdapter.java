package com.fy.baselibrary.base;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * adapter基类
 * @param <Item>
 * Created by fangs on 2017/3/13.
 */
public abstract class CommonAdapter<Item> extends BaseAdapter {

	private Map<Integer, View> viewCache = new HashMap<>();
	protected List<Item> data;
	private Context context;
	private LayoutInflater inflater;

    private int lastCount = 0;

    /**
     * adapter列表同时最大显示数量
     */
    public static final int MAX_ITEM_COUNT = 30;


	public CommonAdapter(Context context, List<Item> data){
		this.context = context;
		this.data = data;
		if(getContext() != null){
			this.inflater = LayoutInflater.from(getContext());
		}
	}
	
	@Override
	public int getCount() {
//        Log.d("CommonAdapter", " getCount() -> "+data.size());
		return data.size();
	}

	@Override
	public Item getItem(int position) {
        Log.d("CommonAdapter", " getItem() -> position "+position);
		return data.get(position);
	}

	@Override
	public long getItemId(int itemId) {
        Log.d("CommonAdapter", " getItemId() -> itemId "+itemId);
		return itemId;
	}

	@Override
	public abstract View getView(int arg0, View arg1, ViewGroup arg2);


    public Map<Integer, View> getViewCache() {
		return viewCache;
	}

	public List<Item> getData() {
		return data;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 设置data,覆盖原有数据
	 * @param data
	 */
	public void setData(List<Item> data){
        Log.e("adapter", " setData data size -> " + data.size());
		this.data = data;
		this.viewCache.clear();
	}
	/**
	 * 添加 data,添加一组data
	 * @param data
	 */
	public void addData(List<Item> data){
        lastCount = data.size();
        Log.e("adapter", " addData before size -> "+this.data.size());

        this.data.addAll(data);

        Log.e("adapter", " addData after size -> "+this.data.size());

        maxCountCheck();
	}


    /**
     * 最大数量检测
     */
    private synchronized void maxCountCheck(){

        if (getViewCache().size() > MAX_ITEM_COUNT){
            Log.d("CommonAdapter", " getViewCache() size("+getViewCache().size()+") >= MAX_ITEM_COUNT("+MAX_ITEM_COUNT+")");
            // 如果超过最大数量，则对于viewCache进行清理
            Log.d("CommonAdapter", " data size -> "+data.size());

            Map<Integer, String> positionMap = new HashMap<>();


            int minValue = getData().size() - MAX_ITEM_COUNT - lastCount;
            // 取出需要清除的 cache的position数组
            for(Integer pos : getViewCache().keySet()){
                if (pos < minValue){
                    positionMap.put(pos, "");
                }
            }

//            for (int i = 0 ; i < getViewCache().size() - MAX_ITEM_COUNT ; i ++){
//                positionMap.put(i, ""+i);
//            }

            // 保留 最后一个元素 - MAX_ITEM_COUNT 这个范围之间的cache
            Log.d("CommonAdapter", " delSize -> "+positionMap.size());

            for (Integer pos : positionMap.keySet()){
                getViewCache().remove(pos);
            }

            Log.d("CommonAdapter", " ViewCache Current Size  -> "+getViewCache().size());
            // 通知listview 界面已经被修改
//            notifyDataSetChanged();
        }
    }

    /**
     * 添加data,从指定location中加入
     * @param location
     * @param data
     */
    public void addData(int location, List<Item> data){
        lastCount = data.size();

        this.data.addAll(location, data);

        maxCountCheck();
    }
	/**
	 * 添加data
	 * @param item
	 */
	public void addData(Item item){
        lastCount = 1;

		this.data.add(item);
        maxCountCheck();
	}

    /**
     * 添加data，从指定location中加入
     * @param location
     * @param item
     */
    public void addData(int location, Item item){
        lastCount = 1;
        this.data.add(location, item);
        maxCountCheck();
    }

    /**
     * 删除指定 Location 位置的data
     * @param location
     */
    public void removeData(int location){
        this.data.remove(location);
    }


	public LayoutInflater getInflater() {
		return inflater;
	}
	/**
	 * 缓存
	 */
	public void clearCache(){
		this.viewCache.clear();
		this.notifyDataSetChanged();
	}

    public void clearCache(int position){
        this.viewCache.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if(observer != null){
            super.unregisterDataSetObserver(observer);
        }
    }

    /**
	 * 清除缓存数据
	 */
	public void clearData(){
		this.data.clear();
		clearCache();
	}

}