package com.select.map.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * description 高德地图  省代码 实体类
 * Created by fangs on 2020/7/14 15:40.
 */
public class ProvinceMap {

    //省份名字
    private String name;
    //简拼
    private String simplicity;
    //全拼
    private String fullPinyin;
    //省份代码
    private String provinceCode;
    //省份下的城市
    private List<CityMap> citys;

    public ProvinceMap() {
        this.name = "";
        this.simplicity = "";
        this.fullPinyin = "";
        this.provinceCode = "";
        this.citys = new ArrayList<>();
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public ProvinceMap setName(String name) {
        this.name = name;
        return this;
    }

    public String getSimplicity() {
        return simplicity == null ? "" : simplicity;
    }

    public ProvinceMap setSimplicity(String simplicity) {
        this.simplicity = simplicity;
        return this;
    }

    public String getFullPinyin() {
        return fullPinyin == null ? "" : fullPinyin;
    }

    public ProvinceMap setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
        return this;
    }

    public String getProvinceCode() {
        return provinceCode == null ? "" : provinceCode;
    }

    public ProvinceMap setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
        return this;
    }

    public List<CityMap> getCitys() {
        if (citys == null) {
            return new ArrayList<>();
        }
        return citys;
    }

    public ProvinceMap setCitys(List<CityMap> citys) {
        this.citys = citys;
        return this;
    }
}
