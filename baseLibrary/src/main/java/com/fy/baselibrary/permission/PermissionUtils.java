package com.fy.baselibrary.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.fy.baselibrary.BuildConfig;
import com.fy.baselibrary.retrofit.ServerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限相关工具类
 * Created by fangs on 2018/8/10 15:07.
 */
public class PermissionUtils {

    private PermissionUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取需要去申请权限的权限列表
     * @param permissions
     * @return
     */
    public static List<String> getRequestPermissionList(Context context, String[] permissions) {
        List<String> requestPermissionCount = new ArrayList<>();
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionCount.add(permission);
            }
        }
        return requestPermissionCount;
    }

    /**
     * 检查是否需要提示用户对该权限的授权进行说明，返回boolean类型
     * @return
     */
    public static List<String> getShouldRationaleList(Activity activity, String... permissions) {
        if (null != permissions) {
            List<String> strings = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    strings.add(permission);
                }
            }
            return strings;
        }
        return null;
    }

    /**
     * 检测权限有没有在清单文件中注册
     * @param activity           Activity对象
     * @param requestPermissions 请求的权限组
     */
    public static void checkPermissions(Activity activity, String... requestPermissions) {
        List<String> manifest = getManifestPermissions(activity);
        if (manifest != null && manifest.size() != 0) {
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new ServerException(permission, 100);
                }
            }
        } else {
            throw new ServerException("清单文件没有注册对应的权限", 100);
        }
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    public static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }


    /**     以下为适配不同厂商手机 进入权限管理界面   */
    private static final String MARK = Build.MANUFACTURER.toLowerCase();

    /**
     * 跳转到应用权限设置页面
     * @param context 上下文对象
     */
    static void jumpPermiSettting(Context context) {

        Intent intent;
        if (MARK.contains("xiaomi")) {
            intent = xiaomi(context);
        } else if (MARK.contains("oppo")) {
            intent = oppo(context);
        } else if (MARK.contains("vivo")) {
            intent = vivo(context);
        } else if (MARK.contains("meizu")) {
            intent = meizu(context);
        } else if (MARK.contains("sony")) {
            intent = sony(context);
        } else if (MARK.contains("lg")) {
            intent = lg(context);
        } else {
            intent = google(context);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            intent = google(context);
            context.startActivity(intent);
        }
    }

    private static Intent google(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }


    private static Intent lg(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        return intent;

    }

    private static Intent sony(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        return intent;

    }

    private static Intent huawei(Context context) {
        Intent intent = new Intent();

        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        if (hasIntent(context, intent)) return intent;

        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"));
        if (hasIntent(context, intent)) return intent;

        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity"));

        return intent;
    }

    private static Intent xiaomi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasIntent(context, intent)) return intent;

        intent.setPackage("com.miui.securitycenter");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        return intent;
    }

    private static Intent oppo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (hasIntent(context, intent)) return intent;
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        if (hasIntent(context, intent)) return intent;
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        return intent;
    }

    private static Intent vivo(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");
        intent.putExtra("packagename", context.getPackageName());
        if (hasIntent(context, intent)) return intent;

        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        return intent;
    }

    private static Intent meizu(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    private static boolean hasIntent(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
