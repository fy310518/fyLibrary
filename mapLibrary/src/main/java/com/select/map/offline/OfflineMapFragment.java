package com.select.map.offline;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.base.fragment.BaseFragment;
import com.fy.baselibrary.rv.adapter.OnListener;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.notify.L;
import com.select.map.R;
import com.select.map.R2;
import com.select.map.bean.CityMap;
import com.select.map.bean.Node;
import com.select.map.bean.ProvinceMap;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * description 离线地图下载 fragment
 * Created by fangs on 2020/7/14 15:07.
 */
public class OfflineMapFragment extends BaseFragment {

    @BindView(R2.id.ev_search_friend)
    EditText ev_search_friend;
    @BindView(R2.id.nodeList)
    RecyclerView nodeList;
    OfflineMapCityListAdapter adapter;

    //离线地图管理实例
    private OfflineMapManager omm;


    @Override
    protected int setContentLayout() {
        return R.layout.map_offline_fm;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void baseInit() {
        initRv();
//                //获取城市列表
//                OfflineMapManager.getOfflineMapCityList()
//                //获取省份列表
//                OfflineMapManager.getOfflineMapProvinceList()
//                //获取已下载的城市列表
//                OfflineMapManager.getDownloadOfflineMapCityList()
//                //获取正在下载的城市列表
//                OfflineMapManager.getDownloadingCityList()

        omm = new OfflineMapManager(getContext(), new OfflineMapManager.OfflineMapDownloadListener() {
            @Override
            public void onDownload(int i, int i1, String s) {
                L.e("下载onDownload" + i1 + "--", s);
            }

            @Override
            public void onCheckUpdate(boolean b, String s) {

            }

            @Override
            public void onRemove(boolean b, String s, String s1) {
                L.e("下载onRemove", s);
            }
        });

        Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String, ObservableSource<List<CityMap>>>() {
                    @Override
                    public ObservableSource<List<CityMap>> apply(String s) throws Exception {
                        //获取城市列表
                        List<OfflineMapProvince> data = omm.getOfflineMapProvinceList();

//                        List<ProvinceMap> provinces = getProvinceData(data);
//                        List<Node> listShowData = mountData(provinces);
//                        return Observable.just(listShowData);

                        List<CityMap> cityData = getCityData(data);
                        return Observable.just(cityData);
                    }
                })
                .subscribe(new Consumer<List<CityMap>>() {
                    @Override
                    public void accept(List<CityMap> nodeList) throws Exception {
                        adapter.setmDatas(nodeList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        omm.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        omm.stop();
    }

    // 按顺序装载数据，根节点pid = 0，生成 列表显示的 数据集合
    private List<Node> mountData(List<ProvinceMap> provinces) {
        //按顺序装载数据，根节点pid = 0，
        final List<Node> allNodes = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < provinces.size(); i++) {

            Node n = new Node(index, 0);
            n.setParentNode(null);
            n.setData(provinces.get(i));
            n.setExpand(false);
            n.setLevel(1);

            List<Node> subNodes = n.getSubNodeList();
            n.setSubNodeList(subNodes);

            List<CityMap> cities = provinces.get(i).getCitys();
            for (int j = 0; j < cities.size(); j++) {
                index++;

                Node one = new Node(index, n.getId());
                one.setParentNode(n);
                one.setData(cities.get(j));

                one.setParentNode(n);
                one.setLevel(2);
                one.setExpand(false);
                subNodes.add(one);
            }

            allNodes.add(n);
            index++;
        }

        return allNodes;
    }

    /**
     * 解析数据，获取 省份list
     */
    private List<ProvinceMap> getProvinceData(List<OfflineMapProvince> data) {
        List<ProvinceMap> result = new ArrayList<>();
        //将获取的省份，城市解析出来
        for (int i = 0; i < data.size(); i++) {
            ArrayList<CityMap> cityList = new ArrayList<>();
            ProvinceMap p = new ProvinceMap();
            p.setName(data.get(i).getProvinceName())
                    .setSimplicity(data.get(i).getJianpin())
                    .setFullPinyin(data.get(i).getPinyin())
                    .setProvinceCode(data.get(i).getProvinceCode())
                    .setCitys(cityList);

            ArrayList<OfflineMapCity> cities = data.get(i).getCityList();

            //这里就不判空操作了，在高德地图源码中已经处理过了
            for (int j = 0; j < cities.size(); j++) {
                CityMap c = new CityMap();
                c.setName(cities.get(j).getCity())
                        .setSimplicity(cities.get(j).getJianpin())
                        .setFullPinyin(cities.get(j).getPinyin())
                        .setCityCode(cities.get(j).getCode())
                        .setProvinceCityCode(cities.get(j).getAdcode());

                cityList.add(c);
            }
            result.add(p);
        }
        return result;
    }

    private List<CityMap> getCityData(List<OfflineMapProvince> data) {
        List<CityMap> cityList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            List<OfflineMapCity> cities = data.get(i).getCityList();

            //这里就不判空操作了，在高德地图源码中已经处理过了
            for (int j = 0; j < cities.size(); j++) {
                CityMap c = new CityMap();
                c.setName(cities.get(j).getCity())
                        .setSimplicity(cities.get(j).getJianpin())
                        .setFullPinyin(cities.get(j).getPinyin())
                        .setCityCode(cities.get(j).getCode())
                        .setProvinceCityCode(cities.get(j).getAdcode());

                cityList.add(c);
            }
        }

        return cityList;
    }

    private void initRv() {
        adapter = new OfflineMapCityListAdapter(getActivity(), new ArrayList<>());
        adapter.setItemClickListner(new OnListener.OnitemClickListener() {
            @Override
            public void onItemClick(View view) {
                CityMap cityMap = (CityMap) view.getTag();
                try {
                    omm.downloadByCityCode(cityMap.getCityCode());
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }
        });
        nodeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        nodeList.setAdapter(adapter);

        ev_search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s); // 当数据改变时，调用过滤器；
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
