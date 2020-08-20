package com.fy.baselibrary.plugin;

import android.app.Activity;
import android.os.Bundle;

/**
 * 接口方式插件化，定义 Activity 生命周期的公共接口
 */
public interface PluginInterface {

    void attach(Activity proxyActivity);

    void onCreate(Bundle saveInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onSaveIntanceState(Bundle outState);

    void onBackPressed();
}
