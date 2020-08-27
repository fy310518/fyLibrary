package com.fy.baselibrary.plugin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.fy.baselibrary.plugin.PluginManager;

import java.lang.reflect.Constructor;

/**
 * description 定义 占坑的服务
 * Created by fangs on 2020/8/25 10:57.
 */
public class ProxyService extends Service {

    private String serviceName;
    private PluginServiceInterface pluginServiceInterface;

    @Override
    public IBinder onBind(Intent intent) {
        initPluginService(intent);
        return pluginServiceInterface.onBind(intent);
    }

    /**
     * 初始化加载插件的Service
     *
     * @param intent
     */
    private void initPluginService(Intent intent) {
        PluginManager.getInstance().setContext(this);
        //获取传递进来的Service的全类名
        serviceName = intent.getStringExtra("serviceName");

        //加载service 类
        try {
            //插件TestService
            Class<?> aClass = getClassLoader().loadClass(serviceName);
            Constructor constructor = aClass.getConstructor(new Class[]{});
            Object in = constructor.newInstance(new Object[]{});

            if (in instanceof PluginServiceInterface) {
                pluginServiceInterface = (PluginServiceInterface) in;
                //传递进去上下文
                pluginServiceInterface.attach(this);

                pluginServiceInterface.onCreate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (pluginServiceInterface == null) {
            initPluginService(intent);
        }
        return pluginServiceInterface.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (null != pluginServiceInterface) pluginServiceInterface.onUnbind(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != pluginServiceInterface) pluginServiceInterface.onDestroy();
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }


}
