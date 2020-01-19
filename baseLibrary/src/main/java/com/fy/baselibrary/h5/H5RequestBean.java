package com.fy.baselibrary.h5;

import android.util.ArrayMap;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * DESCRIPTION：h5 android 交互 test
 * Created by fangs on 2019/3/27 17:03.
 */
public class H5RequestBean {

    /**
     * url :
     * base64:
     * requestMethod : GET
     * header : {"Content-Type":"application/x-www-form-urlencoded","access_token":""}
     * params : {"param1":"value1","param2":"value2","param3":"value3","param4":"value4"}
     * jsMethod :
     */

    private String url = "";
    private ArrayList<String> base64;
    private String requestMethod = "";
    private ArrayMap<String,Object> header;
    private ArrayMap<String,Object> params;
    private String jsMethod = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setHeader(ArrayMap<String, Object> header) {
        if (null == header || header.isEmpty()){
            this.header = new ArrayMap<>();
        } else {
            this.header = header;
        }
    }

    public ArrayMap<String, Object> getHeader() {
        return null == header ? new ArrayMap<>() : header;
    }

    public ArrayMap<String, Object> getParams() {
        return null == params ? new ArrayMap<>() : params;
    }

    public void setParams(ArrayMap<String, Object> params) {
        if (null == params || params.isEmpty()){
            this.params = new ArrayMap<>();
        } else {
            this.params = params;
        }
    }

    public ArrayList<String> getBase64() {
        return null == base64 ? new ArrayList<>() : base64;
    }

    public void setBase64(ArrayList<String> base64) {
        this.base64 = null == base64 ? new ArrayList<>() : base64;
    }

    public String getJsMethod() {
        return jsMethod;
    }

    public void setJsMethod(String jsMethod) {
        this.jsMethod = jsMethod;
    }


}
