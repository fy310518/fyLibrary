package com.fy.baselibrary.retrofit.interceptor;

import com.fy.baselibrary.retrofit.load.down.FileResponseBody;
import com.fy.baselibrary.utils.imgload.imgprogress.ProgressListener;
import com.fy.baselibrary.utils.notify.L;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * describe: 文件下载拦截器
 * Created by fangs on 2019/10/8 22:08.
 */
public class FileDownInterceptor implements Interceptor {

    public static final Map<String, ProgressListener> LISTENER_MAP = new HashMap<>();

    //添加 下载监听
    public static void addListener(String url, ProgressListener listener) {
        LISTENER_MAP.put(url, listener);
    }

    //取消注册下载监听
    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String url = request.url().toString();
        Response response = chain.proceed(request);

        assert response.body() != null;
        return response.newBuilder()
                .body(new FileResponseBody(response.body(), url))
                .build();
    }
}
