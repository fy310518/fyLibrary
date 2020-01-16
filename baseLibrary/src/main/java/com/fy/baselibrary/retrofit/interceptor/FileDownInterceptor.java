package com.fy.baselibrary.retrofit.interceptor;

import com.fy.baselibrary.retrofit.load.down.FileResponseBody;
import com.fy.baselibrary.utils.notify.L;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * describe: 文件下载拦截器
 * Created by fangs on 2019/10/8 22:08.
 */
public class FileDownInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        L.e("fy_file_FileDownInterceptor", "文件下载---" + Thread.currentThread().getName());

        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new FileResponseBody(originalResponse.body()))
                .build();
    }
}
