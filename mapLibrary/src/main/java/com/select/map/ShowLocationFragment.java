package com.select.map;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.fy.baselibrary.aop.annotation.NeedPermission;
import com.fy.baselibrary.base.fragment.BaseFragment;
import com.fy.baselibrary.utils.notify.L;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * describe：显示地图 fragment
 * Created by fangs on 2020/5/19 0019 下午 17:24.
 */
public class ShowLocationFragment extends BaseFragment {

    List<String> locationData;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    @BindView(R2.id.aMap)
    MapView mMapView;

    @BindView(R2.id.txtLocationTitle)
    TextView txtLocationTitle;
    @BindView(R2.id.txtLocationInfo)
    TextView txtLocationInfo;


    @Override
    protected int setContentLayout() {
        return R.layout.show_location_fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        initMap(Double.parseDouble(locationData.get(2)), Double.parseDouble(locationData.get(3)));
//        initLocation();
        showLocationIcon();

        setCurrentLocationDetails(Double.parseDouble(locationData.get(2)), Double.parseDouble(locationData.get(3)));
        return mRootView;
    }

    @Override
    protected void baseInit() {
        Bundle bundle = getArguments();
        assert bundle != null;
        locationData = LocationMsgUtils.parseLocation(bundle.getString("locationUrl"));

        setToolbar(toolbar, locationData.get(0), null);
        txtLocationTitle.setText(locationData.get(0));
    }

    @OnClick({R2.id.imgLocationCenter, R2.id.imgNavigate})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.imgLocationCenter) {
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(Double.parseDouble(locationData.get(3)), Double.parseDouble(locationData.get(2)))));
        } else if (view.getId() == R.id.imgNavigate) {//去导航
            ArrayMap<String, Object> arg = new ArrayMap<>();
            arg.put(ImgUtils.GCJO2_LAT, locationData.get(3));//纬度
            arg.put(ImgUtils.GCJO2_LNG, locationData.get(2));
            arg.put(ImgUtils.DESTINATION, locationData.get(0));
            ImgUtils.goToNaveMap(getContext(), arg);
        }
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
        if (null != mlocationClient) mlocationClient.onDestroy();

        super.onDestroy();
    }

    // 地址逆解析
    private void setCurrentLocationDetails(double longitude, double latitude){
        GeocodeSearch geocoderSearch = new GeocodeSearch(getContext().getApplicationContext());
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int arg1) {
                if (arg1 != 1000) return;
                if (null == regeocodeResult) return;
                String formatAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                txtLocationInfo.setText(formatAddress);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 25, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    AMap aMap;
    private void initMap(double longitude, double latitude) {
        //初始化地图控制器对象
        aMap = mMapView.getMap();

        LatLng latLng = new LatLng(latitude, longitude);
        //改变地图的中心点。 参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15, 30, 0));
        aMap.moveCamera(mCameraUpdate);//设置希望展示的地图缩放级别

        UiSettings mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);//缩放按钮 是否显示
        mUiSettings.setCompassEnabled(false);//指南针 是否显示
        mUiSettings.setMyLocationButtonEnabled(false); //设置定位按钮是否可见。

        //绘制marker
        aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.locate_marker)))
                .draggable(true));
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
//        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
    }

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
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mLocationOption.setInterval(8000);
        //设置是否只定位一次 默认值：false
        mLocationOption.setOnceLocation(false);
        //设置联网超时时间
        mLocationOption.setHttpTimeOut(60000);

        mlocationClient = new AMapLocationClient(getContext().getApplicationContext());
        //设置定位监听
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
                        address = amapLocation.getAddress();

                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        L.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        });

        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }
}
