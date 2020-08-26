package com.fy.baselibrary.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.fy.baselibrary.plugin.activity.ProxyActivity;
import com.fy.baselibrary.utils.JumpUtils;
import com.fy.baselibrary.utils.notify.T;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 插件管理器
 * https://www.jianshu.com/p/eaac8785fa3d
 */
public class PluginManager {

    private Context mContext;

    //插件的资源对象
    private Resources pluginResource;

    //插件的类加载器
    private DexClassLoader dexClassLoader;

    //插件的包信息类
    private PackageInfo packageInfo;

    private static PluginManager pluginManager = new PluginManager();

    private PluginManager() {
    }

    public static PluginManager getInstance() {
        return pluginManager;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    //加载插件apk
    public void loadPlugin(String pluginPath) {
        //获取包管理器
        PackageManager packageManager = mContext.getPackageManager();
        //获取插件的包信息类
        if (null == packageInfo) packageInfo = packageManager.getPackageArchiveInfo(pluginPath, PackageManager.GET_ACTIVITIES);

        //插件解压后的目录
        File pluginFile = mContext.getDir("plugin", Context.MODE_PRIVATE);

        //拷贝插件so库 若插件中存在so，需要拷贝到宿主中才能正常读取;
        String soPath = SoFileUtils.copySo(mContext, pluginPath);
//        SoFileUtils.insertNativeLibraryPathElements(new File(soPath), mContext);

        //获取类加载器
        if (null == dexClassLoader) dexClassLoader = new DexClassLoader(pluginPath, pluginFile.getAbsolutePath(), soPath, mContext.getClassLoader());

        //获取插件的资源对象
        try {
            if (null == pluginResource){
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, pluginPath);
                pluginResource = new Resources(assetManager,
                        mContext.getResources().getDisplayMetrics(),
                        mContext.getResources().getConfiguration());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Resources getPluginResource() {
        return pluginResource;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    /**
     * 加载插件apk
     * @param activity
     * @param apkPath    插件apk 路径【如：/sdcard/Android/data/com.xxx.xxx/files/down/xxxx.apk】
     */
    public static void loadPlugin(Activity activity, String apkPath){
        PluginManager.getInstance().setContext(activity);
        PluginManager.getInstance().loadPlugin(apkPath);
    }

    public void clear(){
        setContext(null);
    }

    /**
     * 跳转到插件中的 指定activity
     * @param activity
     * @param activityName 要启动的 activity 完整包名【如：com.fy.baselibrary.plugin.activity.ProxyActivity】
     */
    public static void jumpPlugin(@NonNull Activity activity, @NonNull String activityName, Bundle bundle) {
        if (isExistence(activityName)){
            if (null == bundle) bundle = new Bundle();
            //由于插件只有一个activity，所以取数组第0个
            bundle.putString("className", activityName);
            JumpUtils.jump(activity, ProxyActivity.class, bundle);
        } else {
            T.showLong("界面不存在");
        }
    }

    /**
     * 跳转到插件中的 指定activity【带回调结果的跳转】
     * @param activity
     * @param activityName
     * @param bundle
     * @param requestCode
     */
    public static void jumpPlugin(@NonNull Activity activity, @NonNull String activityName, Bundle bundle, int requestCode) {
        if (isExistence(activityName)){
            if (null == bundle) bundle = new Bundle();
            //由于插件只有一个activity，所以取数组第0个
            bundle.putString("className", activityName);
            JumpUtils.jump(activity, ProxyActivity.class, bundle, requestCode);
        } else {
            T.showLong("界面不存在");
        }
    }

    /**
     * 判断插件 中是否存在 指定的 activity
     * @param activityName
     */
    public static boolean isExistence(@NonNull String activityName){
        PackageInfo packageInfo = PluginManager.getInstance().getPackageInfo();
        boolean isExistence = false;
        for (ActivityInfo activityInfo : packageInfo.activities) {
            if (activityInfo.name .equals(activityName)) {
                isExistence = true;
                break;
            }
        }

        return isExistence;
    }
}
