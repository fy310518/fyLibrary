package com.select.map;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.rv.adapter.RvCommonAdapter;
import com.fy.baselibrary.utils.ResUtils;
import com.select.map.bean.PoiBean;

import java.util.List;

/**
 * describe：附近的位置 列表适配器
 * Created by fangs on 2020/5/19 0019 上午 11:01.
 */
public class NearbyLocationAdapter extends RvCommonAdapter<PoiBean> {

    public NearbyLocationAdapter(Context context, List<PoiBean> datas) {
        super(context, R.layout.select_location_item, datas);
    }

    @Override
    public void convert(ViewHolder holder, PoiBean poiBean, int position) {
        holder.setText(R.id.txtLocationTitle, poiBean.getTitleName());
        holder.setText(R.id.txtLocationInfo, poiBean.getLocationInfo());

        if (poiBean.isSelected()) mSelectedPos = position;
        setSelectedImg(holder, poiBean.isSelected());

        //    单选 样板代码
        holder.setOnClickListener(R.id.rlSelectLocation, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //实现单选方法三： RecyclerView另一种定向刷新方法：不会有白光一闪动画 也不会重复onBindVIewHolder
                ViewHolder couponVH = (ViewHolder) mRv.findViewHolderForLayoutPosition(mSelectedPos);
                if (couponVH != null) {//还在屏幕里
                    setSelectedImg(couponVH, false);//此处注意判空
                } else {//add by 2016 11 22 for 一些极端情况，holder被缓存在Recycler的cacheView里，
                    //此时拿不到ViewHolder，但是也不会回调onBindViewHolder方法。所以add一个异常处理
                    if (mSelectedPos > -1) notifyItemChanged(mSelectedPos);
                }
                if (mSelectedPos > -1)
                    mDatas.get(mSelectedPos).setSelected(false);//不管在不在屏幕里 都需要改变数据
                //设置新Item的勾选状态
                mSelectedPos = position;
                if (mSelectedPos > -1) mDatas.get(mSelectedPos).setSelected(true);
                setSelectedImg(holder, true);

                if (null != itemClickListner) itemClickListner.onItemClick(holder.itemView);
            }
        });
    }

    private void setSelectedImg(ViewHolder holder, boolean isSelected){
        TextView txtLocationTitle = holder.getView(R.id.txtLocationTitle);
        TextView txtLocationInfo = holder.getView(R.id.txtLocationInfo);
        ImageView imgCheck = holder.getView(R.id.imgCheck);
        if (isSelected){
            int textColor = ResUtils.getColor(R.color.colorMain);
            txtLocationTitle.setTextColor(textColor);
            txtLocationInfo.setTextColor(textColor);
            imgCheck.setVisibility(View.VISIBLE);
        } else {
            txtLocationTitle.setTextColor(ResUtils.getColor(R.color.txtSecondColor));
            txtLocationInfo.setTextColor(ResUtils.getColor(R.color.txtLight));
            imgCheck.setVisibility(View.INVISIBLE);
        }
    }

}
