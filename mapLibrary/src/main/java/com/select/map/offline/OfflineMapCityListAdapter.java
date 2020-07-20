package com.select.map.offline;

import android.content.Context;

import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.rv.adapter.RvCommonAdapter;
import com.select.map.R;
import com.select.map.bean.CityMap;

import java.util.List;

/**
 * description 离线地图 城市列表 适配器
 * Created by fangs on 2020/7/14 16:04.
 */
public class OfflineMapCityListAdapter extends RvCommonAdapter<CityMap> {

    public OfflineMapCityListAdapter(Context context, List<CityMap> datas) {
        super(context, R.layout.map_offline_item, datas);
    }

    @Override
    public boolean filterRule(CityMap value, CharSequence constraint) {
        if (value.getName().contains(constraint) || value.getCityCode().contains(constraint)) return true;
        else return false;
    }

    @Override
    public void convert(ViewHolder holder, CityMap node, int position) {
        holder.setText(R.id.txtCityName, node.getName());
    }


}
