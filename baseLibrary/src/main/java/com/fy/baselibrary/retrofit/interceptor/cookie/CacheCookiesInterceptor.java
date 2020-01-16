package com.fy.baselibrary.retrofit.interceptor.cookie;

import android.annotation.SuppressLint;

import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.cache.SpfAgent;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * describe：缓存网络请求 response 后携带的 cookie;
 * Created by fangs on 2018/3/30.
 */
public class CacheCookiesInterceptor implements Interceptor {

    @SuppressLint("CheckResult")
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (null == chain) L.d("http", "Receivedchain == null");

        Response response = chain.proceed(chain.request());

        List<String> headers = response.headers("set-cookie");
        if (!headers.isEmpty() && headers.size() > 1) {
            StringBuffer cookieBuffer = new StringBuffer();

            Object[] strArray = response.headers("set-cookie").toArray();

            Observable.fromArray(strArray)
                    .map(s -> s.toString().split(";")[0])
                    .subscribe(s -> cookieBuffer.append(s).append(";"));

            SpfAgent.init(Constant.baseSpf)
                    .saveString("cookie", cookieBuffer.toString())
                    .commit(false);

            L.e("http", "CacheCookiesInterceptor" + cookieBuffer.toString());
        }

        return response;
    }
}
