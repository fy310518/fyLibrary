package com.fy.baselibrary.retrofit;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 网络请求 ioc
 * 作为桥梁，沟通调用者和依赖对象
 * Created by fangs on 2017/5/15.
 */
@Singleton
@Component(modules = RequestModule.class)
public interface RequestComponent {

    void inJect(RequestUtils requestConn);

}
