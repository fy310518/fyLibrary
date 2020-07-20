package com.select.map.bean;

import java.io.Serializable;

/**
 * describe：附近的位置 实体类
 * Created by fangs on 2020/5/19 0019 上午 10:43.
 */
public class PoiBean implements Serializable {
    private String titleName;
    private String provinceName;
    private String cityName;
    private String adName;
    private String snippet;
    private double latitude;
    private double longitude;

    private boolean isSelected;//单选 增加参数

    public String getTitleName() {
        return titleName == null ? "" : titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName == null ? "" : titleName;
    }

    public String getCityName() {
        return cityName == null ? "" : cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? "" : cityName;
    }

    public String getAdName() {
        return adName == null ? "" : adName;
    }

    public void setAdName(String adName) {
        this.adName = adName == null ? "" : adName;
    }

    public String getSnippet() {
        return snippet == null ? "" : snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet == null ? "" : snippet;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvinceName() {
        return provinceName == null ? "" : provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName == null ? "" : provinceName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLocationInfo(){
        return getProvinceName() + getCityName() + getAdName() + getSnippet();
    }

    @Override
    public String toString() {
        return "PoiBean{" +
                "titleName='" + titleName + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", adName='" + adName + '\'' +
                ", snippet='" + snippet + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isSelected=" + isSelected +
                '}';
    }
}
