package com.fy.baselibrary.retrofit.interceptor.cache;

import com.fy.baselibrary.utils.net.NetUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * describe： 根据既定逻辑判断是否使用缓存。
 *            判断当前网络是否有效：
 *              1、如果有效,则创建一个请求，该请求能获取一个2秒内未过期的缓存；
 *              2、否则强制获取一个缓存(过期了30天也允许).
 * Created by fangs on 2018/12/6 11:35.
 */
public class IsUseCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        L.e("http--IsUseCache", "intercept()");
        Response response;
        Request request;

        CacheControl cacheControl;
        if (NetUtils.isConnected()) {
            //有网络,检查10秒内的缓存
            cacheControl = new CacheControl.Builder()
                    .maxAge(2, TimeUnit.SECONDS)
                    .build();
        } else {
            //无网络,检查30天内的缓存,即使是过期的缓存
            cacheControl = new CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(30, TimeUnit.DAYS)
                    .build();
        }

        request = chain.request()
                .newBuilder()
                .cacheControl(cacheControl)
                .build();

        response = chain.proceed(request);
        return response.newBuilder().build();
    }
}
