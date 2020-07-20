package com.select.map;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图帮助类
 */
public class LocationMsgUtils {
    /**
     * 构建位置信息的数据【高德文档说明：https://lbs.amap.com/api/webservice/guide/api/staticmaps/】
     * 具体格式："locationUrl=http://restapi.amap.com/v3/staticmap?location=116.481485,39.990464&zoom=10&size=750*300&markers=mid,,A:116.481485,39.990464&key=ee95e52bf08006f63fd29bcfbcf21df0&locationText=武汉市泛海国际SOHO城"
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param adsName   地址名称
     */
    public static String buildAddressContent(String latitude, String longitude, String adsName) {
        //申请的高德地图 web服务api的key
        String gdKey = "3c1190cca00c46248ab33ef67ba15c3a";
        return "locationUrl=http://restapi.amap.com/v3/staticmap?location=" + longitude + "," + latitude +
                "&zoom=15&size=750*300&markers=mid,,A:" + longitude + "," + latitude + "&key=" + gdKey + "&locationText=" + adsName;
    }

    /**
     * 根据 buildAddressContent 方法 的规则，解析 位置数据
     * @param msgContent
     * @return
     */
    public static List<String> parseLocation(String msgContent) {
        List<String> data = new ArrayList<>();
        if (TextUtils.isEmpty(msgContent)) return data;

        int start1 = msgContent.indexOf("&locationText=");
        String adsName = msgContent.substring(start1 + "&locationText=".length());
        String adsPicPath = msgContent.substring("locationUrl=".length(), start1);

        int locationStart = msgContent.indexOf("?location=");
        int locationEnd = msgContent.indexOf("&zoom=");
        String location = msgContent.substring(locationStart + "?location=".length(), locationEnd);
        String[] temp = location.split(",");
        String longitude  = temp[0];
        String latitude   = temp[1];

        data.add(adsName);
        data.add(adsPicPath);
        data.add(longitude);
        data.add(latitude);

        return data;
    }

}
