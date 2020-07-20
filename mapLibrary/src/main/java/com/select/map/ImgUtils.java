package com.select.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.notify.T;

/**
 * DESCRIPTION：门户 图片工具类
 * Created by fangs on 2019/5/7 10:34.
 */
public class ImgUtils {
    private static final String TAG = "ImgUtils";


    //1.百度地图包名
    public static final String BAIDUMAP_PACKAGENAME = "com.baidu.BaiduMap";
    //2.高德地图包名
    public static final String AUTONAVI_PACKAGENAME = "com.autonavi.minimap";
    //3.腾讯地图包名
    public static final String QQMAP_PACKAGENAME = "com.tencent.map";

    public static final String GCJO2_LNG = "gd_lng";
    public static final String GCJO2_LAT = "gd_lat";
    public static final String DESTINATION = "destination";

    //去导航
    public static void goToNaveMap(Context context, ArrayMap<String, Object> arg) {
        if (AppUtils.isPackageExist(context, AUTONAVI_PACKAGENAME)) {
            invokeAuToNaveMap(context, arg);
        } else if (AppUtils.isPackageExist(context, QQMAP_PACKAGENAME)){
            invokeQQMap(context, arg);
        } else {
            T.showLong("暂无导航软件");
        }
    }

    /**
     * 调用高德地图
     * @param context 上下文对象s
     * @param arg     经纬度参数map
     */
    private static void invokeAuToNaveMap(Context context, ArrayMap<String, Object> arg) {
        try {
            Uri uri = Uri.parse("androidamap://route?sourceApplication="
                    + AppUtils.getAppName(context, AppUtils.getLocalPackageName())
                    + "&dlat=" + arg.get(GCJO2_LAT)//终点的纬度
                    + "&dlon=" + arg.get(GCJO2_LNG)//终点的经度
                    + "&dname=" + arg.get(DESTINATION)////终点的显示名称
                    + "&dev=0&m=0&t=0");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            intent.addCategory("android.intent.category.DEFAULT");

            context.startActivity(intent);
        } catch (Exception e) {
            L.e(TAG, e.getMessage());
        }
    }

    /**
     * 调用腾讯地图
     * @param context 上下文对象s
     * @param arg     经纬度参数map
     */
    private static void invokeQQMap(Context context, ArrayMap<String, Object> arg) {
        try {
            Uri uri = Uri.parse("qqmap://map/routeplan?type=drive"
                    + "&to=" + arg.get(DESTINATION)//终点的显示名称 必要参数
                    + "&tocoord=" + arg.get(GCJO2_LAT) + "," + arg.get(GCJO2_LNG)//终点的经纬度
                    + "&referer=" + AppUtils.getAppName(context, AppUtils.getLocalPackageName()));
            Intent intent = new Intent();
            intent.setData(uri);

            context.startActivity(intent);
        } catch (Exception e) {
            L.e(TAG, e.getMessage());
        }
    }

    /**
     * 调用百度地图----------------
     * @param context 上下文对象
     * @param arg     参数
     */
    private static void invokeBaiDuMap(Context context, ArrayMap<String, Object> arg) {
        try {
            Uri uri = Uri.parse("baidumap://map/geocoder?" +
                    "location=" + arg.get(GCJO2_LAT) + "," + arg.get(GCJO2_LNG)
                    + "&name=" + arg.get(DESTINATION) //终点的显示名称
                    +  "&coord_type=gcj02");//坐标 （百度同样支持他自己的db0911的坐标，但是高德和腾讯不支持）
            Intent intent = new Intent();
            intent.setPackage(BAIDUMAP_PACKAGENAME);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            L.e(TAG, e.getMessage());
        }
    }

}
