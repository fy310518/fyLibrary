package com.fy.baselibrary.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.drawable.ShapeBuilder;
import com.fy.baselibrary.utils.notify.L;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * App相关的辅助类
 *
 * Created by fangs on 2017/3/1.
 */
public class AppUtils {

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * Android 7.0 在应用间共享文件
     * 获取 清单文件注册的 文件共享签名认证
     */
    public static String getFileProviderName() {
        return getLocalPackageName() + ".provider";
    }

    /**
     * 获取当前应用的 应用id
     * @return 应用id（包名）
     */
    public static String getLocalPackageName() {
        try {
            Context context = ConfigUtils.getAppCtx();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "获取失败";
        }
    }

    /**
     * 获取 指定应用ID 的 应用程序名称
     * @return appName
     * @return packageName  context.getPackageName()
     */
    public static String getAppName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
//            int labelRes = packageInfo.applicationInfo.labelRes;
//            return context.getResources().getString(labelRes);
//            方式二
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "未知名称";
        }
    }

    /**
     * 获取 指定应用ID 的 应用图标
     * @param packageName
     */
    public static Drawable getAppLogo(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationIcon(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return ShapeBuilder.create().build();
        }
    }

    /**
     * 获取 指定应用ID 的 应用 版本号 versionCode
     * @param context
     * @param packageName
     */
    public static int getVersionCode(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取 指定应用ID 的 应用程序 版本名称 versionName
     * @return versionName
     */
    public static String getVersionName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0.0.0";
        }
    }


    /**
     * 根据安装包路径获取 包名
     * @param context
     * @param filePath 安装包路径
     * @return
     */
    public static String getPackageName(Context context, String filePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return appInfo.packageName;  //得到安装包名称
        }
        return "";
    }

    /**
     * 根据安装包路径获取 版本号
     * @param context
     * @param filePath
     */
    public static int getPathVersionCode(Context context, String filePath){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.versionCode;
        }
        return 0;
    }

    /**
     * 根据安装包路径获取 版本名称
     * @param filePath
     */
    public static String getPathVersionName(Context context, String filePath){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);

        if (null != info) {
            return info.versionName;
        } else {
            return "0.0.0";
        }
    }

    /**
     * 根据安装包路径获取 应用图标
     * @param filePath
     */
    public static Drawable getPathLogo(Context context, String filePath){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        ApplicationInfo applicationInfo = info.applicationInfo;

        if (applicationInfo != null) {
            return packageManager.getApplicationIcon(applicationInfo);
        } else {
            return ShapeBuilder.create().build();
        }
    }


    /**
     * 检查手机上【是否】安装了指定的软件
     * @param packageName 包名
     */
    public static boolean isPackageExist(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo pckInfo = packageManager.getPackageInfo(packageName, 0);

            if (null != pckInfo) return true;
        } catch (PackageManager.NameNotFoundException e) {
            L.e("TDvice", e.getMessage());
        }
        return false;
    }

    /**
     * 检查手机上是否安装了指定的软件 （方式二 复杂版本）
     * @param context
     * @param packageName 包名
     */
    public static boolean isAvailable(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


    /**
     * 返回应用程序的签名
     * @param packageName The name of the package.
     * @return the application's signature
     */
    public static Signature[] getAppSignature(final String packageName) {
        Context context = ConfigUtils.getAppCtx();

        if (isSpace(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断app是否在前台还是在后台运行
     * @return true/false
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
//                BACKGROUND=400 EMPTY=500 FOREGROUND=100
//                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200

                int pid = appProcess.pid;

                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

                    L.i(context.getPackageName(), "处于后台" + appProcess.processName);
                    return true;
                } else {
                    L.i(context.getPackageName(), "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前应用 进程id
     * @return id
     */
    public static int getProcessId(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
//                BACKGROUND=400 EMPTY=500 FOREGROUND=100
//                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200

                return appProcess.pid;

            }
        }
        return -1;
    }

    /**
     * 获取进程号对应的进程名
     * @param pid 进程号 [android.os.Process.myPid()]
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取栈顶 activity 的 ComponentName 对象
     * @param context
     * @return
     */
    public static ComponentName getTopActivity(Context context) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityInfo aInfo = null;
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list.size() != 0) {
            ActivityManager.RunningTaskInfo topRunningTask = list.get(0);
            return topRunningTask.topActivity;
        } else {
            return null;
        }
    }


    /**
     * 获取手机ip
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 收集设备信息
     */
    public StringBuffer collectPhoneInfo(StringBuffer sb) {
        Context ctx = ConfigUtils.getAppCtx();
        PackageManager pm = ctx.getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                sb.append("\n").append(pi.versionName == null ? "null" : pi.versionName).append("\n");

                sb.append("\n").append(pi.versionCode).append("\n");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return sb;
    }

    /**
     * 导出异常信息到SD卡中
     * @param e
     * @return 返回文件名称
     */
    @SuppressLint("CommitPrefEdits")
    public static StringBuffer dumpExceptionToSDCard(Throwable e, StringBuffer sb) {
        /* 获得错误信息字符串 */
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            e.printStackTrace(pw);
            String result = sw.toString();

            sb.append("\n").append(result).append("\n");
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                sw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            pw.close();
        }

        return sb;
    }

}
