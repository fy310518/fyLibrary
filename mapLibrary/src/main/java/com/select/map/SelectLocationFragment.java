package com.select.map;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.fy.baselibrary.aop.annotation.NeedPermission;
import com.fy.baselibrary.base.fragment.BaseFragment;
import com.fy.baselibrary.rv.adapter.OnListener;
import com.fy.baselibrary.rv.divider.ListItemDecoration;
import com.fy.baselibrary.utils.JumpUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.notify.T;
import com.select.map.R2;
import com.select.map.bean.PoiBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * describe：选择地理位置 fragment
 * Created by fangs on 2020/5/18 0018 下午 17:53.
 */
public class SelectLocationFragment extends BaseFragment {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    @BindView(R2.id.aMap)
    MapView mMapView;

    @BindView(R2.id.rvNearby)
    RecyclerView rvNearby;
    NearbyLocationAdapter adapter;

    @Override
    protected int setContentLayout() {
        return R.layout.select_location_fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        initMap();
        initLocation();
        showLocationIcon();
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_item_txt, menu);
        View menuLayout = menu.findItem(R.id.menuSchedule).getActionView();

        TextView btn_comment_add = menuLayout.findViewById(R.id.btn_comment_add);
        btn_comment_add.setText(R.string.app_confirm);

        menuLayout.setOnClickListener(v -> {
            AmapNaviPage.getInstance().showRouteActivity(getContext(), new AmapNaviParams(null), new INaviInfoCallback() {
                @Override
                public void onInitNaviFailure() {

                }

                @Override
                public void onGetNavigationText(String s) {

                }

                @Override
                public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

                }

                @Override
                public void onArriveDestination(boolean b) {

                }

                @Override
                public void onStartNavi(int i) {

                }

                @Override
                public void onCalculateRouteSuccess(int[] ints) {

                }

                @Override
                public void onCalculateRouteFailure(int i) {

                }

                @Override
                public void onStopSpeaking() {

                }

                @Override
                public void onReCalculateRoute(int i) {

                }

                @Override
                public void onExitPage(int i) {

                }

                @Override
                public void onStrategyChanged(int i) {

                }

                @Override
                public View getCustomNaviBottomView() {
                    return null;
                }

                @Override
                public View getCustomNaviView() {
                    return null;
                }

                @Override
                public void onArrivedWayPoint(int i) {

                }

                @Override
                public void onMapTypeChanged(int i) {

                }

                @Override
                public View getCustomMiddleView() {
                    return null;
                }

                @Override
                public void onNaviDirectionChanged(int i) {

                }

                @Override
                public void onDayAndNightModeChanged(int i) {

                }

                @Override
                public void onBroadcastModeChanged(int i) {

                }

                @Override
                public void onScaleAutoChanged(boolean b) {

                }
            });
        });//点击事件
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected void baseInit() {
        setToolbar(toolbar, ResUtils.getStr(R.string.chat_send_location), null);

        initRv();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if(null != mlocationClient) mlocationClient.onDestroy();

        super.onDestroy();
    }

    private void initRv(){
        adapter = new NearbyLocationAdapter(getContext(), new ArrayList<>());
        adapter.setmRv(rvNearby);
        adapter.setItemClickListner(new OnListener.OnitemClickListener() {
            @Override
            public void onItemClick(View view) {
                PoiBean poiBean = (PoiBean) view.getTag();
                T.showLong(poiBean.getLocationInfo());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(poiBean.getLatitude(), poiBean.getLongitude())));
            }
        });
        rvNearby.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNearby.addItemDecoration(new ListItemDecoration.Builder().setmSpace(R.dimen.itemSplit_line).create(getContext()));
        rvNearby.setAdapter(adapter);
    }

    AMap aMap;
    private void initMap(){
        //初始化地图控制器对象
        aMap = mMapView.getMap();

        //改变地图的中心点。 参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(39.977290, 116.337000), 15, 30, 0));
        aMap.moveCamera(mCameraUpdate);//设置希望展示的地图缩放级别

        UiSettings mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);//缩放按钮 是否显示
        mUiSettings.setCompassEnabled(false);//指南针 是否显示
        mUiSettings.setMyLocationButtonEnabled(true); //设置定位按钮是否可见。

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() { //对amap添加移动地图事件监听器
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {}
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                LatLng latLng = cameraPosition.target;
                doSearchQuery("", latLng.latitude, latLng.longitude);
            }
        });
    }

    public void showLocationIcon() {
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色

        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。

        aMap.setMyLocationStyle(myLocationStyle);  //图片与map 相互关联
        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
    }

    private LocationSource.OnLocationChangedListener mListener;
    //声明mlocationClient对象
    private AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;

    private double defaultPosition = 0.0;
    private double sLongitude = defaultPosition; // 经度
    private double sLatitude = defaultPosition; // 纬度
    private String address = "未获取到位置信息"; // 经度
    @NeedPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void initLocation() {
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
                if (mlocationClient == null) {//激活定位
                    //初始化定位参数
                    mLocationOption = new AMapLocationClientOption();
                    //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    //设置定位间隔,单位毫秒,默认为2000ms
                    // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                    // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                    // 在定位结束后，在合适的生命周期调用onDestroy()方法
                    // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                    mLocationOption.setInterval(1000);
                    //设置是否只定位一次 默认值：false
                    mLocationOption.setOnceLocation(false);
                    //设置联网超时时间
                    mLocationOption.setHttpTimeOut(30000);

                    //初始化定位
                    mlocationClient = new AMapLocationClient(getContext().getApplicationContext());
                    //设置定位参数
                    mlocationClient.setLocationOption(mLocationOption);
                    //设置定位回调监听
                    mlocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation amapLocation) {
                            if (amapLocation != null) {
                                if (amapLocation.getErrorCode() == 0) {
                                    //定位成功回调信息，设置相关消息
                                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                                    amapLocation.getAccuracy();//获取精度信息
                                    sLongitude = amapLocation.getLongitude();//获取经度
                                    sLatitude = amapLocation.getLatitude();//获取纬度

                                    if (address.equals("未获取到位置信息"))aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(sLatitude, sLongitude)));
                                    address   = amapLocation.getAddress();

                                    L.e("AmapError", "location Error, ErrCode:" + address);

                                    if (null != adapter && adapter.getItemCount() == 0) doSearchQuery("", sLatitude, sLongitude);
                                } else {
                                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                                    L.e("AmapError", "location Error, ErrCode:"
                                            + amapLocation.getErrorCode() + ", errInfo:"
                                            + amapLocation.getErrorInfo());
                                }
                            }
                        }
                    });
                    mlocationClient.startLocation();//启动定位
                }
            }

            @Override
            public void deactivate() {
                mListener = null;
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
        });
    }

    PoiSearch.Query query;
    PoiSearch poiSearch;
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String city, double latitude, double longitude) {
        String mType = "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";
        query = new PoiSearch.Query("", mType, city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);// 设置查第一页
        poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int arg1) { //解析result获取POI信息
                if (arg1 != 1000) return;
                if (null == result || null == result.getQuery()) return;
                if (!result.getQuery().equals(query)) return;// 是否是同一条

                List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                List<PoiBean> data = new ArrayList();
                if (poiItems != null && poiItems.size() > 0) {
                    for (int i = 0; i < poiItems.size(); i++) {
                        PoiItem poiItem = poiItems.get(i);   //写一个bean，作为数据存储

                        PoiBean bean = new PoiBean();
                        bean.setTitleName(poiItem.getTitle());
                        bean.setProvinceName(poiItem.getProvinceName());
                        bean.setCityName(poiItem.getCityName());
                        bean.setAdName(poiItem.getAdName());
                        bean.setSnippet(poiItem.getSnippet());
                        bean.setLatitude(poiItem.getLatLonPoint().getLatitude());
                        bean.setLongitude(poiItem.getLatLonPoint().getLongitude());
                        bean.setSelected(i == 0);

//                        云创咖啡,湖北省,武汉市,江汉区,江旺路6号火凤凰一楼,30.623,114.237054
                        L.e("PoiResult", "" + poiItem.getTitle() + "," + poiItem.getProvinceName() + ","
                                + poiItem.getCityName() + ","
                                + poiItem.getAdName() + ","//区
                                + poiItem.getSnippet() + ","
                                + poiItem.getLatLonPoint() + "\n");
                        data.add(bean);
                    }

                    adapter.setmDatas(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {
            }
        });
        // 设置搜索区域为以lp点为圆心，其周围1000米范围
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 1000, true));
        poiSearch.searchPOIAsyn();// 异步搜索
    }

}
