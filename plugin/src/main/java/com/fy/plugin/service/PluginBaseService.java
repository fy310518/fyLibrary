package com.fy.plugin.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.IBinder;

import com.fy.plugin.PluginManager;


/**
 * description 接口方式插件化 --》插件app 父类 Service
 * Created by fangs on 2020/8/25 11:22.
 */
public class PluginBaseService extends Service implements PluginServiceInterface {

    protected Service mProxyService;

    @Override
    public void attach(Service proxyService) {
        this.mProxyService = proxyService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (null == mProxyService) {
            return null;
        }
        return null;
    }

    @Override
    public void onCreate() {
        if (null == mProxyService) {
            super.onCreate();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == mProxyService) {
            return super.onStartCommand(intent, flags, startId);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (null == mProxyService) {
            super.onDestroy();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (null == mProxyService) {
            return super.onUnbind(intent);
        }
        return false;
    }

    @Override
    public Context getBaseContext() {
        if (null == mProxyService) {
            return super.getBaseContext();
        } else {
            return mProxyService.getBaseContext();
        }
    }

    @Override
    public Context getApplicationContext() {
        if (null == mProxyService) {
            return super.getApplicationContext();
        } else {
            return mProxyService.getApplicationContext();
        }
    }

    @Override
    public Resources getResources() {
        if (null != mProxyService) {
            return PluginManager.getInstance().getPluginResource();
        } else {
            return super.getResources();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (null != mProxyService) {
            return mProxyService.getClassLoader();
        } else {
            return super.getClassLoader();
        }
    }

    @Override
    public String getPackageName() {
        if (null != mProxyService) {
            return mProxyService.getPackageName();
        } else {
            return super.getPackageName();
        }
    }

    @Override
    public PackageManager getPackageManager() {
        if (null != mProxyService) {
            return mProxyService.getPackageManager();
        } else {
            return super.getPackageManager();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (null != mProxyService) {
            return mProxyService.getSystemService(name);
        } else {
            return super.getSystemService(name);
        }
    }

    @Override
    public ContentResolver getContentResolver() {
        if (null != mProxyService) {
            return mProxyService.getContentResolver();
        } else {
            return super.getContentResolver();
        }
    }

}
