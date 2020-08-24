package com.fy.baselibrary.plugin;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Constructor;

/**
 * 接口插件化：代理activity
 * Created by zjp on 2020/6/30 13:36
 */
public class ProxyActivity extends AppCompatActivity {

    //需要加载插件的全类名
    private String className;
    private PluginInterface pluginInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取到真正目的地的类名
        className = getIntent().getStringExtra("className");
        try {
            //通过类加载器去加载这个类
            Class<?> aClass = getClassLoader().loadClass(className);
            Constructor constructor = aClass.getConstructor(new Class[]{});
            //将它实例化
            Object obj = constructor.newInstance(new Object[]{});
            if (obj instanceof PluginInterface) {
                pluginInterface = (PluginInterface) obj;
                pluginInterface.attach(this);
                //调用的是插件中的Activity的onCreate方法
                pluginInterface.onCreate(savedInstanceState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (pluginInterface != null)
            pluginInterface.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pluginInterface != null)
            pluginInterface.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pluginInterface != null)
            pluginInterface.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pluginInterface != null)
            pluginInterface.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pluginInterface != null)
            pluginInterface.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (pluginInterface != null)
            pluginInterface.onSaveInstanceStates(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pluginInterface != null)
            pluginInterface.onActivityResult(requestCode, resultCode, data);
    }

    //重写加载类
    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    //重写加载资源
    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getPluginResource();
    }
}
