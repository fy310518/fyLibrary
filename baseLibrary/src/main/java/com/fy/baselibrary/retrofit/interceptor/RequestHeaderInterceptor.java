package com.fy.baselibrary.retrofit.interceptor;

import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.cache.SpfAgent;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * describe： 请求头拦截器；1、添加请求头；2、默认请求地址替换为动态请求地址（前提：请求接口配置了请求头：@Headers({"url_name:user"})）
 * Created by fangs on 2018/12/7 11:05.
 */
public class RequestHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取request
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", ConfigUtils.getContentType())
//                .addHeader("Accept-Encoding", "gzip, deflate")//根据服务器要求添加（避免重复压缩乱码）
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .addHeader("app-type", "Android")
//                .addHeader("Access-Control-Allow-Origin", "*")
//                .addHeader("Access-Control-Allow-Headers", "X-Requested-With")
//                .addHeader("Vary", "Accept-Encoding")
                .header(ConfigUtils.getTokenKey(), SpfAgent.init("").getString(Constant.token))
                .build();

        //获取request的创建者builder
        Request.Builder builder = request.newBuilder();

        //从request中获取headers，通过给定的键url_name
        List<String> headerValues = request.headers("url_name");
        if (headerValues != null && headerValues.size() > 0) {
            //如果有这个header，先将配置的header删除，因为 此header仅用作app和okhttp之间使用
            builder.removeHeader("url_name");

            //匹配获得新的BaseUrl
            String headerValue = headerValues.get(0);
            HttpUrl newBaseUrl = null;
            if ("user".equals(headerValue) && !TextUtils.isEmpty(Constant.custom_Url)) {
                newBaseUrl = HttpUrl.parse(Constant.custom_Url);

                //从request中获取原有的HttpUrl实例oldHttpUrl
                HttpUrl oldHttpUrl = request.url();
                //重建新的HttpUrl，修改需要修改的url部分
                HttpUrl newFullUrl = oldHttpUrl
                        .newBuilder()
                        .scheme(newBaseUrl.scheme())
                        .host(newBaseUrl.host())
                        .port(newBaseUrl.port())
                        .build();

                //重建这个request，通过builder.url(newFullUrl).build()；
                //然后返回一个response至此结束修改
                return chain.proceed(builder.url(newFullUrl).build());
            }
        }

        return chain.proceed(request);
    }
}
