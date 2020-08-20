package com.fy.baselibrary.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 接口方式插件化
 * 插件app 父类 activity
 */
public class PluginBaseActivity extends AppCompatActivity implements PluginInterface {

    private Activity mProxyActivity;

    @Override
    public void attach(Activity proxyActivity) {
        this.mProxyActivity = proxyActivity;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle saveInstanceState) {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onStart() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onResume() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onPause() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onStop() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveIntanceState(Bundle outState) {

    }

    @Override
    public void setContentView(View view) {
        if (null != mProxyActivity) {
            mProxyActivity.setContentView(view);
        } else
            super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (null != mProxyActivity) {
            mProxyActivity.setContentView(layoutResID);
        } else
            super.setContentView(layoutResID);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return mProxyActivity.findViewById(id);
    }

    @Override
    public Intent getIntent() {
        return mProxyActivity.getIntent();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mProxyActivity.getClassLoader();
    }

    //插件内 activity 启动另一个activity
    public void jumpPlugin() {
        PackageInfo packageInfo = PluginManager.getInstance().getPackageInfo();

        Intent intent = new Intent("com.zjp.plugin.ProxyActivity");
        //由于插件只有一个activity，所以取数组第0个
        intent.putExtra("className", packageInfo.activities[0].name);
        mProxyActivity.startActivity(intent);
    }
}
