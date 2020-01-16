package com.fy.baselibrary.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.fy.baselibrary.aop.resultfilter.ActResultManager;
import com.fy.baselibrary.aop.resultfilter.ResultCallBack;
import com.fy.baselibrary.utils.notify.T;

import java.io.File;

/**
 * 界面跳转工具类
 * Created by fangs on 2017/5/9.
 */
public class JumpUtils {

    private JumpUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 从fragment 跳转到指定的 activity
     * @param fragment
     * @param bundle
     * @param actClass
     */
    public static void jump(Fragment fragment, Class actClass, Bundle bundle) {
        Intent intent = new Intent(fragment.getContext(), actClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        fragment.startActivity(intent);
    }

    /**
     * 从 activity 跳转到指定的 activity
     * @param actClass
     * @param bundle
     */
    public static void jump(Activity act, Class actClass, Bundle bundle) {
        Intent intent = new Intent(act, actClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        act.startActivity(intent);
//        第一个参数 下一界面进入效果；第二个参数 当前界面退出效果
//        act.overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_left_out);
    }

    /**
     * 从fragment 跳转到指定 Action 的activity
     * @param fragment
     * @param action
     * @param bundle
     */
    public static void jump(Fragment fragment, String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        fragment.startActivity(intent);
    }

    /**
     * 从 activity 跳转到指定 Action 的activity
     * @param act
     * @param action
     * @param bundle
     */
    public static void jump(Activity act, String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        act.startActivity(intent);
    }

    /**
     * 跳转到指定 Action 的activity 带回调结果的跳转
     * @param action    要跳转到的 action
     * @param bundle
     * @param requestCode 请求码
     */
    public static void jump(Activity act, String action, Bundle bundle, int requestCode) {
        Intent intent = new Intent(action);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        act.startActivityForResult(intent, requestCode);//原生默认
    }

    /**
     * 跳转到指定 Action 的activity 带回调结果的跳转
     * @param action    要跳转到的 action
     * @param bundle
     * @param requestCode 请求码
     * @param callBack 回调结果，回调接口
     */
    public static void jump(Activity act, String action, Bundle bundle, int requestCode, ResultCallBack callBack) {
        Intent intent = new Intent(action);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        ActResultManager.getInstance()
                .startActivityForResult(act, intent, requestCode, callBack);
    }


    /**
     * 跳转到指定 activity  带回调结果的跳转
     * @param actClass    要跳转到的Activity
     * @param bundle
     * @param requestCode 请求码
     */
    public static void jump(Activity act, Class actClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(act, actClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        act.startActivityForResult(intent, requestCode);//原生默认
    }

    /**
     * 跳转到指定 activity  带回调结果的跳转
     * @param actClass    要跳转到的Activity
     * @param bundle
     * @param requestCode 请求码
     * @param callBack    回调结果，回调接口
     */
    public static void jump(Activity act, Class actClass, Bundle bundle, int requestCode, ResultCallBack callBack) {
        Intent intent = new Intent(act, actClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        ActResultManager.getInstance()
                .startActivityForResult(act, intent, requestCode, callBack);
    }


// 传统方式 fragment 通过startActivityForResult() 启动新的activity 有接收不到返回结果的问题，解决方案如下：
//  一.只嵌套了一层Fragment（比如activity中使用了viewPager，viewPager中添加了几个Fragment） 在这种情况下要注意几个点：
//  1.在Fragment中使用startActivityForResult的时候，不要使用getActivity().startActivityForResult,而是应该直接使startActivityForResult()。
//  2.如果activity中重写了onActivityResult，那么activity中的onActivityResult一定要加上：
//      super.onActivityResult(requestCode, resultCode, data)。
//  如果违反了上面两种情况，那么onActivityResult只能够传递到activity中的，无法传递到Fragment中的。
//  没有违反上面两种情况的前提下，可以直接在Fragment中使用startActivityForResult和onActivityResult，和在activity中使用的一样。
//  二：使用aop 在Activity的onActivityResult() 执行之后，通过回调接口 获取返回结果
//      使用方式同 activity 跳转到指定 activity  带回调结果的跳转

    /**
     * 从fragment 跳转到指定的 activity; 带回调结果的跳转
     * @param fragment
     * @param actClass
     * @param bundle
     * @param requestCode
     */
    public static void jump(Fragment fragment, Class actClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), actClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        fragment.startActivityForResult(intent, requestCode);//原生默认
    }

    /**
     * 从fragment 跳转到指定的 activity; 带回调结果的跳转
     * @param fragment
     * @param actClass
     * @param bundle
     * @param requestCode
     * @param callBack
     */
    public static void jump(Fragment fragment, Class actClass, Bundle bundle, int requestCode, ResultCallBack callBack) {
        Intent intent = new Intent(fragment.getContext(), actClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

//        fragment.startActivityForResult(intent, requestCode);//原生默认
        ActResultManager.getInstance()
                .startActivityForResult(fragment.getActivity(), intent, requestCode, callBack);
    }

    /**
     * 从fragment 跳转到指定 action 的 activity; 带回调结果的跳转
     * @param fragment
     * @param action
     * @param bundle
     * @param requestCode
     */
    public static void jump(Fragment fragment, String action, Bundle bundle, int requestCode, ResultCallBack callBack) {
        Intent intent = new Intent(action);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

//        fragment.startActivityForResult(intent, requestCode);
        ActResultManager.getInstance()
                .startActivityForResult(fragment.getActivity(), intent, requestCode, callBack);
    }


//////////////////////////////////反射跳转 start///////////////////////////////////////////////
    /**
     * 使用反射 跳转到指定 路径的 activity
     * @param act
     * @param bundle
     * @param classPath
     */
    public static void jumpReflex(Activity act, Bundle bundle, String classPath){
        try {
            Class cla = Class.forName(classPath);
            jump(act, cla, bundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用反射 跳转到指定 路径的 activity
     * @param fragment
     * @param bundle
     * @param classPath
     */
    public static void jumpReflex(Fragment fragment, Bundle bundle, String classPath){
        try {
            Class cla = Class.forName(classPath);
            jump(fragment, cla, bundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用反射 从fragment 跳转到指定 路径的 activity; 带回调结果的跳转
     * @param fragment
     * @param bundle
     * @param classPath
     * @param requestCode
     */
    public static void jumpReflex(Fragment fragment, Bundle bundle, String classPath, int requestCode, ResultCallBack callBack){
        try {
            Class cla = Class.forName(classPath);
            jump(fragment, cla, bundle, requestCode, callBack);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用反射 跳转到指定 路径的 activity; 带回调结果的跳转
     * @param act
     * @param bundle
     * @param classPath
     * @param callBack 回调接口
     */
    public static void jumpReflex(Activity act, String classPath, Bundle bundle, int requestCode, ResultCallBack callBack){
        try {
            Class cla = Class.forName(classPath);
            jump(act, cla, bundle, requestCode, callBack);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//////////////////////////////////反射跳转 end///////////////////////////////////////////////

    /**
     * 退出当前activity 并带数据回到上一个Activity
     * @param act
     * @param bundle 可空
     */
    public static void jumpResult(Activity act, Bundle bundle){
        Intent intent = new Intent();
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        act.setResult(Activity.RESULT_OK, intent);
        act.finish();
    }

    /**
     * 退出当前activity
     */
    public static void exitActivity(Activity act) {
        act.finish();
    }

    /**
     * 返回桌面
     * @param act
     */
    public static void backDesktop(Activity act){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        act.startActivity(intent);
    }

    /**
     * 退出整个应用
     * @param act
     */
    public static void exitApp(Activity act, Class actClass){
        Intent intent = new Intent(act, actClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意
        intent.putExtra("exitApp", true);//添加标记
        act.startActivity(intent);
    }

    /**
     * 启动指定 url 的 第三方应用 界面
     * @param act
     * @param url  如："gc://pull.gc.circle/conn/start?type=110"
     * @param bundle 这里Intent当然也可传递参数,但是一般情况下都会放到上面的URL中进行传递
     */
    public static void jumpUrl(Activity act, String url, Bundle bundle){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setData(uri);

        if (null != bundle) {
            intent.putExtras(bundle);
        }

        act.startActivity(intent);
    }

    /**
     * 启动指定 包名 的 第三方应用
     * @param act
     * @param packageName
     * @param bundle
     */
    public static void jumpPackage(Activity act, String packageName, Bundle bundle) {
        PackageManager packageManager = act.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            act.startActivity(intent);
        } catch (Exception e) {
            T.showLong("应用未安装");
            e.printStackTrace();
        }
    }

    /**
     * 启动指定 包名 的第三方应用 的指定 路径的 activity
     * @param act
     * @param packageName 目标应用 应用id
     * @param path        目标activity路径
     * @param bundle
     */
    public static void jumpPackageAct(Activity act, String packageName, String path, Bundle bundle) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, path);
        intent.setComponent(componentName);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        try {
            act.startActivity(intent);
        } catch (Exception e) {
            T.showLong("应用未安装");
            e.printStackTrace();
        }
    }

    /**
     * 启动指定 包名 的第三方应用 的指定 路径的 activity, 带回调结果的跳转
     * @param act
     * @param packageName
     * @param path
     * @param bundle
     * @param requestCode
     */
    public static void jumpPackageAct(Activity act, String packageName, String path, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, path);
        intent.setComponent(componentName);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        try {
            act.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            T.showLong("应用未安装");
            e.printStackTrace();
        }
    }

    /**
     * 启动指定 包名 的第三方应用 的指定 路径的 activity, 带回调结果的跳转
     * @param fragment
     * @param packageName
     * @param path
     * @param bundle
     * @param requestCode
     */
    public static void jumpPackageAct(Fragment fragment, String packageName, String path, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, path);
        intent.setComponent(componentName);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        try {
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            T.showLong("应用未安装");
            e.printStackTrace();
        }
    }

    /**
     * 跳转到浏览器 打开指定 URL链接
     * @param act
     * @param url
     */
    public static void jump(Activity act, String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        act.startActivity(intent);
    }

    /**
     * 跳转到 对应 action的设置界面
     * @param act     如：Settings.ACTION_APPLICATION_DETAILS_SETTINGS(权限设置)
     * @param action
     */
    public static void jumpSettting(Activity act, String action){
        Intent localIntent = new Intent();
        localIntent.setAction(action);
        localIntent.setData(Uri.fromParts("package", act.getPackageName(), null));
        act.startActivity(localIntent);
    }


    /**
     * 拨打电话
     * 直接拨打电话 需要申请权限：<uses-permission android:name="android.permission.CALL_PHONE" />
     * 手动点击拨打 不需要权限
     * @param action  [Intent.ACTION_DIAL(手动点击拨打), Intent.ACTION_CALL(直接拨打电话)]
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context ctx, String action, String phoneNum) {
        if (Validator.isMobile(phoneNum) || Validator.isPhone(phoneNum)){
            Intent intent = new Intent(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            ctx.startActivity(intent);
        } else {
            T.showLong("请使用正确的号码！");
        }
    }

    /**
     * 调用系统安装器安装apk(适配 Android 7.0 在应用间共享文件)
     *
     * @param context 上下文
     * @param file apk文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, AppUtils.getFileProviderName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载软件
     *
     * @param context
     * @param packageName
     */
    public static void uninstallApk(Context context, String packageName) {
        if (AppUtils.isPackageExist(context, packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            context.startActivity(uninstallIntent);
        }
    }


    /**
     * 国内手机厂商白名单跳转工具类
     * @return
     */
    public static void getSettingIntent(Context context){
        ComponentName componentName = null;

        String brand = android.os.Build.BRAND;

        switch (brand.toLowerCase()){
            case "samsung":
                componentName = new ComponentName("com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                break;
            case "huawei":
                componentName = new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                break;
            case "xiaomi":
                componentName = new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
                break;
            case "vivo":
                componentName = new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
                break;
            case "oppo":
                componentName = new ComponentName("com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                break;
            case "360":
                componentName = new ComponentName("com.yulong.android.coolsafe",
                        "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
                break;
            case "meizu":
                componentName = new ComponentName("com.meizu.safe",
                        "com.meizu.safe.permission.SmartBGActivity");
                break;
            case "oneplus":
                componentName = new ComponentName("com.oneplus.security",
                        "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
                break;
            default:
                break;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(componentName!=null){
            intent.setComponent(componentName);
        }else{
            intent.setAction(Settings.ACTION_SETTINGS);
        }

        try {
            context.startActivity(intent);
        }catch (Exception e){
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    /**
     * 是否在白名单内
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isSystemWhiteList(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        String packageName = context.getPackageName();
        boolean isWhite = pm.isIgnoringBatteryOptimizations(packageName);
        return isWhite;
    }
}
