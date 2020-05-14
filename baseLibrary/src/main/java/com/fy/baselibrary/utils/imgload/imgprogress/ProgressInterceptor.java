package com.fy.baselibrary.utils.imgload.imgprogress;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * okhttp 连接器捕获http 通讯，计算下载进度
 */
public class ProgressInterceptor implements Interceptor {

    public static final Map<String, ProgressListener> LISTENER_MAP = new HashMap<>();

    //入注册下载监听
    public static void addListener(String url, ProgressListener listener) {
        LISTENER_MAP.put(url, listener);
    }

    //取消注册下载监听
    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();

        Response response = chain.proceed(request);

        return response.newBuilder()
                .body(new ProgressResponseBody(url, response.body()))
                .build();
    }

}
