package com.select.map.bean;

/**
 * description 高德地图  城市代码 实体类
 * Created by fangs on 2020/7/14 15:36.
 */
public class CityMap {
    //城市名称
    private String name;
    //城市代码
    private String cityCode;
    //简拼
    private String simplicity;
    //全拼
    private String fullPinyin;
    //与省份的关系代码，注：跟省份代码不同
    private String provinceCityCode;

    public CityMap() {
        this.name = "";
        this.cityCode = "";
        this.simplicity = "";
        this.fullPinyin = "";
        this.provinceCityCode = "";
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public CityMap setName(String name) {
        this.name = name;
        return this;
    }

    public String getCityCode() {
        return cityCode == null ? "" : cityCode;
    }

    public CityMap setCityCode(String cityCode) {
        this.cityCode = cityCode;
        return this;
    }

    public String getSimplicity() {
        return simplicity == null ? "" : simplicity;
    }

    public CityMap setSimplicity(String simplicity) {
        this.simplicity = simplicity;
        return this;
    }

    public String getFullPinyin() {
        return fullPinyin == null ? "" : fullPinyin;
    }

    public CityMap setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
        return this;
    }

    public String getProvinceCityCode() {
        return provinceCityCode == null ? "" : provinceCityCode;
    }

    public CityMap setProvinceCityCode(String provinceCityCode) {
        this.provinceCityCode = provinceCityCode;
        return this;
    }
}
