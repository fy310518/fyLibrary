package com.fy.baselibrary.application.ioc;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 提供依赖对象
 * Created by fangs on 2018/7/13.
 */
@Module
public class ConfigModule {

    private Context context;
    private ConfigUtils.ConfigBiuder  biuder;

    public ConfigModule(Context context, ConfigUtils.ConfigBiuder  biuder) {
        this.context = context;
        this.biuder  = biuder;
    }



    @Singleton
    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public ConfigUtils.ConfigBiuder provideCer() {
        return biuder;
    }
}
