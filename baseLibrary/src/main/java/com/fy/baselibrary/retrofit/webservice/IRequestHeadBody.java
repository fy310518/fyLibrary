package com.fy.baselibrary.retrofit.webservice;

/**
 * description 定义 soap:Body 接口 用于设置 泛型 R：所对应的 请求实体类
 * Created by fangs on 2021/1/8 11:02.
 */
public interface IRequestHeadBody<R> {

    /**
     * 设置 请求参数
     * @param model
     */
    void setRequestModel(R model);
}
