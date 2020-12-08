package com.fy.baselibrary.launchericon;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.fy.baselibrary.application.BaseLifecycleCallback;
import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.notify.L;

import java.util.LinkedHashMap;

/**
 * description 桌面图标 管理器
 * Created by fangs on 2020/12/8 10:48.
 */
public class LauncherIconManager {

    /** 切换图标任务Map */
    private static LinkedHashMap<String, SwitchIconTask> taskMap = new LinkedHashMap<>();

    /**
     * 添加 切换图标任务
     * @param iconTasks
     */
    public static void addNewTask(SwitchIconTask... iconTasks){
        for (SwitchIconTask iconTask : iconTasks){
            //防止重复添加 任务
            if (taskMap.containsKey(iconTask.getAliasComponentClassName())) continue;

            taskMap.put(iconTask.getAliasComponentClassName(), iconTask);
        }
    }

    /**
     * 注册监听应用运行状态，根据条件 切换图标
     * @param application
     * 【需要在manifest文件 注册 启动也别名 模板如下：】
     *      <activity-alias
     *             android:name=".SplashAlias2Activity"
     *             android:enabled="false"
     *             android:icon="@mipmap/ic_launcher_11_11"
     *             android:targetActivity=".SplashActivity">
     *             <intent-filter>
     *                 <action android:name="android.intent.action.MAIN" />
     *                 <category android:name="android.intent.category.LAUNCHER" />
     *              </intent-filter>
     *      </activity-alias>
     */
    public static void register(Application application){
        application.registerActivityLifecycleCallbacks(new BaseLifecycleCallback() {
            int startedActivityCount;
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (startedActivityCount == 0) L.e("");
                startedActivityCount++;
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                startedActivityCount--;
                if (startedActivityCount == 0) proofreadingInOrder(application);
            }
        });
    }

    //循环所有任务，校对 预设时间
    private static void proofreadingInOrder(Context context){
        for (SwitchIconTask iconTask : taskMap.values()){
            if (proofreading(context, iconTask)) break;
        }
    }

    /**
     * 校对预设时间/过期时间
     * @param context
     * @param task
     * @return true 已过预设时间      false 未达预设时间或已过期
     */
    private static boolean proofreading(Context context, SwitchIconTask task){
        long currentTime = System.currentTimeMillis();
        if (currentTime > task.getOutDateTime()){//任务是否 是否过期
            disableComponent(context, AppUtils.getLauncherActivityName(context));
            enableComponent(context, task.getLauncherComponentClassName());
            return false;
        } else if (currentTime > task.getPresetTime()){//任务是否 超过预设时间
            disableComponent(context, AppUtils.getLauncherActivityName(context));
            enableComponent(context, task.getAliasComponentClassName());
            return true;
        } else {
            return false;
        }
    }


    /**
     * 启用组件
     * @param context 上下文
     * @param className 组件类名
     */
    private static void enableComponent(Context context, String className) {
        ComponentName componentName = new ComponentName(context, className);

        if (isComponentEnabled(context, componentName)) return; //已经启用

        context.getPackageManager().setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    /**
     * 禁用组件
     * @param context 上下文
     * @param className 组件类名
     */
    private static void disableComponent(Context context, String className) {
        ComponentName componentName = new ComponentName(context, className);

        if (isComponentDisabled(context, componentName)) return;  // 已经禁用

        context.getPackageManager().setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    /**
     * 组件是否处于可用状态
     * @param context 上下文
     * @param componentName 组件名
     */
    public static boolean isComponentEnabled(Context context, ComponentName componentName) {
        int state = context.getPackageManager().getComponentEnabledSetting(componentName);
        return PackageManager.COMPONENT_ENABLED_STATE_ENABLED == state;
    }

    /**
     * 组件是否处于禁用状态
     * @param context 上下文
     * @param componentName 组件名
     */
    public static boolean isComponentDisabled(Context context, ComponentName componentName) {
        int state = context.getPackageManager().getComponentEnabledSetting(componentName);
        return PackageManager.COMPONENT_ENABLED_STATE_DISABLED == state;
    }
}
