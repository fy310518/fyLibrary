package com.fy.baselibrary.retrofit.webservice;

/**
 * description 定义 soap:Body 接口 用于获取 泛型 R：所对应的 实体类
 * Created by fangs on 2021/1/8 11:33.
 */
public interface IResponseBody<P> {

    /**
     * 获取 响应实体类
     * @return P
     */
    P getResponseModel();
}
