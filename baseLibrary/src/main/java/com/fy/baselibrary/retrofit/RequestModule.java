package com.fy.baselibrary.retrofit;

import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.converter.file.FileConverterFactory;
import com.fy.baselibrary.retrofit.interceptor.FileDownInterceptor;
import com.fy.baselibrary.retrofit.interceptor.RequestHeaderInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cache.CacheNetworkInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cache.IsUseCacheInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cookie.AddCookiesInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cookie.CacheCookiesInterceptor;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.security.SSLUtil;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLSocketFactory;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 提供依赖对象的实例
 * Created by fangs on 2017/5/15.
 */
@Module
public class RequestModule {

    @Singleton
    @Provides
    protected Retrofit getService(RxJava2CallAdapterFactory callAdapterFactory, GsonConverterFactory
            gsonConverterFactory, OkHttpClient.Builder okBuilder) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(new FileConverterFactory())
                .addConverterFactory(gsonConverterFactory)
                .baseUrl(ConfigUtils.getBaseUrl())
                .client(okBuilder.build())
                .build();
    }

    @Singleton
    @Provides
    protected RxJava2CallAdapterFactory getCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Singleton
    @Provides
    protected GsonConverterFactory getGsonConvertFactory() {
        return GsonConverterFactory.create(new GsonBuilder()
                .setLenient()// json宽松
                .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
                .serializeNulls() //智能null
                .setPrettyPrinting()// 调教格式
                .disableHtmlEscaping() //默认是GSON把HTML 转义的
                .create());
//        return DES3GsonConverterFactory.create();//使用 自定义 GsonConverter
    }

    @Singleton
    @Provides
    protected OkHttpClient.Builder getClient(HttpLoggingInterceptor logInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(Constant.DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .readTimeout(Constant.DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .writeTimeout(Constant.DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//错误重连
                .addInterceptor(new RequestHeaderInterceptor())
                .addInterceptor(new FileDownInterceptor())
                .addNetworkInterceptor(logInterceptor)
                .addInterceptor(new CacheCookiesInterceptor())
                .addNetworkInterceptor(new AddCookiesInterceptor())
                .hostnameVerifier((hostname, session) -> {
                    return true;//强行返回true 即验证成功
                })
                .protocols(Collections.singletonList(Protocol.HTTP_1_1));

        if (ConfigUtils.isEnableCacheInterceptor()) {//是否 添加缓存拦截器
            builder.addInterceptor(new IsUseCacheInterceptor())
                    .addNetworkInterceptor(new CacheNetworkInterceptor())
                    .cache(new Cache(FileUtils.folderIsExists(FileUtils.cache + ".ok-cache", 1), 1024 * 1024 * 30L));
        }

        List<Interceptor> interceptors = ConfigUtils.getInterceptor();
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }

        InputStream is = null;
        if (!TextUtils.isEmpty(ConfigUtils.getCer())){
            is = new Buffer().writeUtf8(ConfigUtils.getCer()).inputStream();
        } else if (!TextUtils.isEmpty(ConfigUtils.getCerFileName())){
            is = ResUtils.getAssetsInputStream(ConfigUtils.getCerFileName());
        }

        if (null != is){
            SSLSocketFactory sslSocketFactory = SSLUtil.getSSLSocketFactory(is);
            if (null != sslSocketFactory) {
                builder.sslSocketFactory(sslSocketFactory);
            }
        }

        return builder;
    }

    @Singleton
    @Provides
    protected HttpLoggingInterceptor getResponseIntercept() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                L.e("net 请求or响应", message);
//                FileUtils.fileToInputContent("log", "日志.txt", message);
            }
        });
        if (ConfigUtils.isDEBUG()) loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        return loggingInterceptor;
    }

}
