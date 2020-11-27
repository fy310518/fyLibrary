package com.fy.baselibrary.utils.imgload.imgprogress;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.utils.security.SSLUtil;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * 更改Glide配置，替换Glide 默认http通讯组件
 */
@GlideModule
public class OkHttpLibraryGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);

        //添加拦截器到Glide
        OkHttpClient.Builder builder = RequestUtils.getOkBuilder();
        builder.addInterceptor(new ProgressInterceptor());
        //加载图片 信任所有证书
        builder.sslSocketFactory(SSLUtil.createSSLSocketFactory());
        builder.hostnameVerifier(SSLUtil.DO_NOT_VERIFY);
        OkHttpClient okHttpClient = builder.build();

        //原来的是  new OkHttpUrlLoader.Factory()；
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(okHttpClient));
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    }

    //完全禁用清单解析
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
