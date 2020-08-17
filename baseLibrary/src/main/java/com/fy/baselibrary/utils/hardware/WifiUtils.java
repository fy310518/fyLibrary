package com.fy.baselibrary.utils.hardware;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * description wifi 工具类【需要如下权限】
 *      <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
 *     <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
 *     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 *     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 *     <uses-permission android:name="android.permission.INTERNET"/>
 * Created by fangs on 2020/8/14 15:35.
 */
public class WifiUtils {
    private static final String TAG = "WifiUtils";

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    private WifiUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取本机 wifi管理器
     */
    private static WifiManager getWifiManager() {
        return (WifiManager) ConfigUtils.getAppCtx().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /** 关闭 wifi功能 */
    public static boolean closeWifi() {
        boolean bRet = true;
        WifiManager wifiManager = getWifiManager();
        if (wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(false);
        }
        return bRet;
    }

    /** 打开 wifi功能 */
    public static boolean openWifi() {
        boolean bRet = true;
        WifiManager wifiManager = getWifiManager();
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    /**
     * 创建热点
     * @param mSSID   热点名称
     * @param mPasswd 热点密码
     * @param isOpen  是否是开放热点
     */
    public static void startWifiAp(String mSSID, String mPasswd, boolean isOpen) {
        WifiManager wifiManager = getWifiManager();
            //如果wifi处于打开状态，则关闭wifi,
        if (wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(false);

        try {
            Method method1 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();

            netConfig.SSID = mSSID;
            netConfig.preSharedKey = mPasswd;
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            if (isOpen) {
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            } else {
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            }
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            method1.invoke(wifiManager, netConfig, true);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭热点
     */
    public static void closeWifiAp() {
        WifiManager wifiManager = getWifiManager();
        if (isWifiApEnabled()) {
            try {
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(wifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取热点名
     **/
    public static String getApSSID() {
        try {
            WifiManager wifiManager = getWifiManager();
            Method localMethod = wifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (localMethod == null) return null;
            Object localObject1 = localMethod.invoke(wifiManager, new Object[0]);
            if (localObject1 == null) return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null) return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
            if (localField1 == null) return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null) return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null) return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    /**
     * 检查是否开启Wifi热点
     */
    public static boolean isWifiApEnabled() {
        try {
            WifiManager wifiManager = getWifiManager();
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 开热点手机 获得其他连接手机IP的方法
     * @return 其他手机IP 数组列表
     */
    public static List<String> getConnectedIP() {
        List<String> connectedIp = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    if (!ip.equalsIgnoreCase("ip")) {
                        connectedIp.add(ip);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return connectedIp;
    }

    /**
     * 搜索wifi热点 ；【扫描结果 通过注册广播获取】
     * private BroadcastReceiver receiver = new BroadcastReceiver() {
     *         @Override
     *         public void onReceive(Context context, Intent intent) {
     *             final String action = intent.getAction();
     *             if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
     *                 // wifi已成功扫描到可用wifi。
     *                 List<ScanResult> scanResults = wifiManager.getScanResults();
     *             }
     *     };
     */
    public static void searchWifi() {
        WifiManager wifiManager = getWifiManager();
        if (!wifiManager.isWifiEnabled()) {
            //开启wifi
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
    }

    /**
     *  连接WiFi；传入要连接的无线网
     * @param SSID
     * @param Password
     * @param Type
     */
    public static boolean connect(String SSID, String Password, WifiCipherType Type) {
        if (!openWifi()) {
            return false;
        }

        WifiManager wifiManager = getWifiManager();
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (getWifiManager().getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        WifiConfiguration wifiConfig = CreateWifiInfo(SSID, Password, Type);
        //
        if (wifiConfig == null) {
            L.e(TAG, "====wifiConfig == null====");
            return false;
        }

        WifiConfiguration tempConfig = isExsits(SSID);
        if (tempConfig != null) {
            getWifiManager().removeNetwork(tempConfig.networkId);
        }
        int netID = getWifiManager().addNetwork(wifiConfig);
        boolean bRet = getWifiManager().enableNetwork(netID, true);
        return bRet;
    }

    /**
     * 配置连接
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    public static WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
//        WifiConfiguration config = new WifiConfiguration();
//        config.allowedAuthAlgorithms.clear();
//        config.allowedGroupCiphers.clear();
//        config.allowedKeyManagement.clear();
//        config.allowedPairwiseCiphers.clear();
//        config.allowedProtocols.clear();
//        config.SSID = "\"" + SSID + "\"";
//        config.SSID = SSID;
//        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
//            config.wepKeys[0] = "";
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
//        }
//        if (Type == WifiCipherType.WIFICIPHER_WEP) {
//            //   config.preSharedKey = "\"" + Password + "\"";
//            config.hiddenSSID = true;
//            config.wepKeys[0] = "\"" + Password + "\"";
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
//        }
//        if (Type == WifiCipherType.WIFICIPHER_WPA) {
//            config.preSharedKey = "\"" + Password + "\"";
//            config.status = WifiConfiguration.Status.ENABLED;
//        } else {
//            return null;
//        }
//        return config;
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = isExsits(SSID);
        if (tempConfig != null) {
            getWifiManager().removeNetwork(tempConfig.networkId);
        }

        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {// WIFICIPHER_NOPASS
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {// WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WPA) {// WIFICIPHER_WPA
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    /**
     * 切换到指定wifi
     * @param wifiName 指定的wifi名字
     * @param wifiPwd  wifi密码，如果已经保存过密码，可以传入null
     */
    public boolean changeToWifi(String wifiName, String wifiPwd) {
        List<WifiConfiguration> wifiList = getWifiManager().getConfiguredNetworks();
        WifiConfiguration wfc = null;
        for (int i = 0; i < wifiList.size(); ++i) {
            WifiConfiguration wifiInfo0 = wifiList.get(i);
            if (wifiInfo0.SSID.contains(wifiName)) {
                L.e(TAG, "===wifi已连接过===" + wifiInfo0.SSID);
//                return doChange2Wifi(wifiInfo0.networkId);
                wfc = wifiInfo0;
            }
        }
        L.e(TAG, "====wificonfig为空=====" + (wfc != null));
        if (wfc != null) {
            if (getWifiManager().startScan()) {
                List<ScanResult> scanResults = getWifiManager().getScanResults();
                for (ScanResult s : scanResults) {
                    L.e(TAG, "====扫描到wifi名称=====" + s.SSID);
                    if (s.SSID.contains(wifiName)) {
                        L.e(TAG, "====正好扫描到该wifi，现在去连接=====");
                        return doChange2Wifi(wfc.networkId);
                    }
                }
            }
        }
        L.e(TAG, "======建立新的wifi连接=====");
        if (getWifiManager().startScan()) {
            List<ScanResult> scanResults = getWifiManager().getScanResults();
            L.e(TAG, "======扫描得到wifi个数=====" + scanResults.size());
            for (ScanResult s : scanResults) {
                L.e(TAG, "======扫描到wifi名称====" + s.SSID);
                if (s.SSID.contains(wifiName)) {
                    L.e(TAG, "====开始连接指定wifi===" + s.SSID);
                    WifiConfiguration wifiNewConfiguration = CreateWifiInfo(s.SSID, wifiPwd, WifiCipherType.WIFICIPHER_NOPASS);
                    int newNetworkId = getWifiManager().addNetwork(wifiNewConfiguration);
                    if (newNetworkId == -1) {
                        L.e(TAG, "=====操作失败,需要您到手机wifi列表中取消对设备连接的保存====");
                        removeWifi(s.SSID, null);
                    } else {
                        return doChange2Wifi(newNetworkId);
                    }
                }
            }
            return false;
        } else {
            L.e(TAG, "======开启wifi扫描失败=====");
            return false;
        }
    }

    //连接WiFi
    private static boolean doChange2Wifi(int newNetworkId) {
        L.e(TAG, "=====开始切换wifi=====");
        // 如果wifi 开关没打开（1、先打开wifi，2，使用指定的wifi)
        openWifi();

        boolean enableNetwork = getWifiManager().enableNetwork(newNetworkId, true);
        if (!enableNetwork) {
            L.e(TAG, "===切换到指定wifi失败===");
            return false;
        } else {
            L.e(TAG, "===切换到指定wifi成功===");
            return true;
        }
    }

    /**
     * 删除一个链接过的wifi
     * @param wifiName
     * @param wifiPwd
     */
    public static void removeWifi(String wifiName, String wifiPwd) {
        if (wifiPwd == null) {
            WifiConfiguration wifiNewConfiguration = CreateWifiInfo(wifiName, wifiPwd, WifiCipherType.WIFICIPHER_NOPASS);
            if (wifiNewConfiguration != null) {
                getWifiManager().removeNetwork(wifiNewConfiguration.networkId);
            }
        }
    }

    /**
     * 查看以前是否也配置过这个网络
     */
    public static WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = getWifiManager().getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

}
