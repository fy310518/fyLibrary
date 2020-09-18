package com.fy.baselibrary.permission;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.fy.baselibrary.BuildConfig;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.ServerException;
import com.fy.baselibrary.utils.os.OSUtils;

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
     */
    public static List<String> getRequestPermissionList(Context context, String... permissions) {
        List<String> requestPermissionCount = new ArrayList<>();
        for (String permission : permissions){
            if (!isPermissionGranted(context, permission)) {
                requestPermissionCount.add(permission);
            }
        }
        return requestPermissionCount;
    }

    /**
     * 判断某个权限是否授予
     */
    private static boolean isPermissionGranted(Context context, String permission) {
        // 如果是安卓 6.0 以下版本就默认授予
        if (!OSUtils.isAndroid6()) return true;

        // 检测存储权限
        if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) return hasStoragePermission(context);

        // 检测安装权限
        if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) return hasInstallPermission(context);

        // 检测悬浮窗权限
        if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) return hasWindowPermission(context);

        // 检测通知栏权限
        if (Permission.NOTIFICATION_SERVICE.equals(permission)) return hasNotifyPermission(context);

        // 检测系统权限
        if (Permission.WRITE_SETTINGS.equals(permission)) return hasSettingPermission(context);

        if (!OSUtils.isAndroid10()) {
            // 检测 10.0 的三个新权限，如果当前版本不符合最低要求，那么就用旧权限进行检测
            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return true;
            } else if (Permission.ACTIVITY_RECOGNITION.equals(permission)) {
                return context.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (!OSUtils.isAndroid8()) {
            // 检测 8.0 的两个新权限，如果当前版本不符合最低要求，那么就用旧权限进行检测
            if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                return context.checkSelfPermission(Permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED;
            } else if (Permission.READ_PHONE_NUMBERS.equals(permission)) {
                return context.checkSelfPermission(Permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
            }
        }

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查是否需要提示用户对该权限的授权进行说明
     * 【其实是 判断申请的权限列表 是否被永久拒绝】
     */
    public static List<String> getShouldRationaleList(Activity activity, String... permissions) {
        if (null == permissions) return null;

        List<String> strings = new ArrayList<>();
        for (String permission : permissions) {
            if (isPermissionPermanentDenied(activity, permission)) {
                strings.add(permission);
            }
        }
        return strings;
    }

    /**
     * 判断某个权限是否被永久拒绝
     * @param activity              Activity对象
     * @param permission            请求的权限
     */
    private static boolean isPermissionPermanentDenied(Activity activity, String permission) {
        if (!OSUtils.isAndroid6()) return false;

        // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
        if (isSpecialPermission(permission)) return false;

        if (!OSUtils.isAndroid10()) {
            // 检测 10.0 的三个新权限，如果当前版本不符合最低要求，那么就用旧权限进行检测
            if (Permission.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    Permission.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return false;
            } else if (Permission.ACTIVITY_RECOGNITION.equals(permission) ) {
                return activity.checkSelfPermission(Permission.BODY_SENSORS) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!OSUtils.isAndroid8()) {
            // 检测 8.0 的两个新权限，如果当前版本不符合最低要求，那么就用旧权限进行检测
            if (Permission.ANSWER_PHONE_CALLS.equals(permission)) {
                return activity.checkSelfPermission(Permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission);
            } else if (Permission.READ_PHONE_NUMBERS.equals(permission) ) {
                return activity.checkSelfPermission(Permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED &&
                        !activity.shouldShowRequestPermissionRationale(permission);
            }
        }

        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED &&
                !activity.shouldShowRequestPermissionRationale(permission);
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

    /**
     * 当前应用 是否开启指定的特殊权限
     */
    public static boolean isAppSpecialPermission(Context context, String specialPermission) {
        if (specialPermission.equals(Permission.MANAGE_EXTERNAL_STORAGE)) {
            return PermissionUtils.hasStoragePermission(context);
        } else if (specialPermission.equals(Permission.REQUEST_INSTALL_PACKAGES)) {
            return PermissionUtils.hasInstallPermission(context);
        } else if (specialPermission.equals(Permission.SYSTEM_ALERT_WINDOW)) {
            return PermissionUtils.hasWindowPermission(context);
        } else if (specialPermission.equals(Permission.NOTIFICATION_SERVICE)) {
            return PermissionUtils.hasNotifyPermission(context);
        } else if (specialPermission.equals(Permission.WRITE_SETTINGS)) {
            return PermissionUtils.hasSettingPermission(context);
        }

        return false;
    }

    /**
     * 是否有存储权限
     */
    public static boolean hasStoragePermission(Context context) {
        if (OSUtils.isAndroid11()) {
            return Environment.isExternalStorageManager();
        } else {
            List<String> requestPermission = PermissionUtils.getRequestPermissionList(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
            return requestPermission.size() == 0;
        }
    }

    /**
     * 是否有安装权限
     */
    public static boolean hasInstallPermission(Context context) {
        if (OSUtils.isAndroid8()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    static boolean hasWindowPermission(Context context) {
        if (OSUtils.isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 是否有通知栏权限
     * 参考 Support 库中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled();
     */
    public static boolean hasNotifyPermission(Context context) {
        if (OSUtils.isAndroid7()) {
            return context.getSystemService(NotificationManager.class).areNotificationsEnabled();
        } else {
            return true;
        }
    }

    /**
     * 是否有系统设置权限
     */
    public static boolean hasSettingPermission(Context context) {
        if (OSUtils.isAndroid6()) {
            return Settings.System.canWrite(context);
        }
        return true;
    }

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    public static boolean containsSpecialPermission(List<String> permissions) {
        if (null == permissions || permissions.isEmpty()) return false;

        for (String permission : permissions) {
            if (isSpecialPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    public static boolean isSpecialPermission(String permission) {
        return Permission.MANAGE_EXTERNAL_STORAGE.equals(permission) ||
                Permission.REQUEST_INSTALL_PACKAGES.equals(permission) ||
                Permission.SYSTEM_ALERT_WINDOW.equals(permission) ||
                Permission.NOTIFICATION_SERVICE.equals(permission) ||
                Permission.WRITE_SETTINGS.equals(permission);
    }

    /**
     * 根据权限 获取 权限组
     * @param sunPermission
     */
    public static String getPermissionGroup(String sunPermission){
        if (OSUtils.isAndroid11()) {
            return Permission.permissionMap.get(sunPermission);
        } else if (OSUtils.isAndroid10()){
            return Permission.permissionMap.get(sunPermission);
        } else {
            try {
                return ConfigUtils.getAppCtx().getPackageManager().getPermissionInfo(sunPermission, 0).group;
            } catch (PackageManager.NameNotFoundException e) {
            }
            return "";
        }
    }


    // 如果是被永久拒绝就跳转到应用权限系统设置页面
    public static void startPermissionActivity(Fragment fragment, List<String> deniedPermissions) {
        try {
            fragment.startActivityForResult(getSmartPermissionIntent(fragment.getContext(), deniedPermissions), PermissionFragment.PERMISSION_REQUEST_CODE);
        } catch (Exception ignored) {
            fragment.startActivityForResult(jumpPermiSettting(fragment.getContext()), PermissionFragment.PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 根据传入的权限自动选择最合适的权限设置页
     */
    static Intent getSmartPermissionIntent(Context context, List<String> deniedPermissions) {
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            return jumpPermiSettting(context);
        }

        // 如果失败的权限里面包含了特殊权限
        if (PermissionUtils.containsSpecialPermission(deniedPermissions)) {
            // 如果当前只有一个权限被拒绝了
            if (deniedPermissions.size() == 1) {
                String permission = deniedPermissions.get(0);
                if (Permission.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
                    return getStoragePermissionIntent(context);
                } else if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {
                    return getInstallPermissionIntent(context);
                } else if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
                    return getWindowPermissionIntent(context);
                } else if (Permission.NOTIFICATION_SERVICE.equals(permission)) {
                    return getNotifyPermissionIntent(context);
                } else if (Permission.WRITE_SETTINGS.equals(permission)) {
                    return getSettingPermissionIntent(context);
                } else {
                    return jumpPermiSettting(context);
                }
            } else {
                // 跳转到应用详情界面
                return jumpPermiSettting(context);
            }
        } else {
            // 跳转到具体的权限设置界面
            return jumpPermiSettting(context);
        }
    }


    /**
     * 判断传入意图 的 Activity 是否存在
     */
    public static boolean hasActivityIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 获取悬浮窗权限设置界面意图
     */
    static Intent getWindowPermissionIntent(Context context) {
        Intent intent = null;
        if (OSUtils.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }

        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = jumpPermiSettting(context);
        }
        return intent;
    }

    /**
     * 获取安装权限设置界面意图
     */
    static Intent getInstallPermissionIntent(Context context) {
        Intent intent = null;
        if (OSUtils.isAndroid8()) {
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = jumpPermiSettting(context);
        }
        return intent;
    }

    /**
     * 获取通知栏权限设置界面意图
     */
    static Intent getNotifyPermissionIntent(Context context) {
        Intent intent = null;
        if (OSUtils.isAndroid8()) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = jumpPermiSettting(context);
        }
        return intent;
    }

    /**
     * 获取系统设置权限界面意图
     */
    static Intent getSettingPermissionIntent(Context context) {
        Intent intent = null;
        if (OSUtils.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = jumpPermiSettting(context);
        }
        return intent;
    }

    /**
     * 获取存储权限设置界面意图
     */
    static Intent getStoragePermissionIntent(Context context) {
        Intent intent = null;
        if (OSUtils.isAndroid11()) {
            intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        }
        if (intent == null || !PermissionUtils.hasActivityIntent(context, intent)) {
            intent = jumpPermiSettting(context);
        }
        return intent;
    }

    /**     以下为适配不同厂商手机 进入权限管理界面   */
    private static final String MARK = Build.MANUFACTURER.toLowerCase();
    /**
     * 获取 到应用权限设置页面 的意图
     * @param context 上下文对象
     */
    public static Intent jumpPermiSettting(Context context) {
        Intent intent = null;
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
        }

        if (null == intent || PermissionUtils.hasActivityIntent(context, intent)){
            intent = google(context);
        }
        return intent;
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
