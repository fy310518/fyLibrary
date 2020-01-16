package com.fy.baselibrary.retrofit.interceptor.cache;

import com.fy.baselibrary.utils.notify.L;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * describe： OKHTTP 网络缓存拦截器；
 *            主要是在缓存没命中的情况下,请求网络后,修改返回头,加上Cache-Control,告知OKHTTP对该请求进行一个60秒的缓存.
 * Created by fangs on 2018/12/6 11:30.
 */
public class CacheNetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        L.e("http--Cache", "intercept()");

        Response response = chain.proceed(chain.request());
        if (response != null && response.isRedirect()) {
            //如果是重定向，则不做缓存
            return response;
        } else {
            //无缓存,进行缓存
            assert response != null;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    //对请求进行最大60秒的缓存
                    .addHeader("Cache-Control", "max-age=60")
                    .build();
        }
    }
}
