package com.fy.baselibrary.plugin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * description </p>
 * Created by fangs on 2020/8/25 10:54.
 */
public interface PluginServiceInterface {

    void attach(Service proxyService);

    IBinder onBind(Intent intent);

    boolean onUnbind(Intent intent);

    void onCreate();

    int onStartCommand(Intent intent, int flags, int startId);

    void onDestroy();

}
