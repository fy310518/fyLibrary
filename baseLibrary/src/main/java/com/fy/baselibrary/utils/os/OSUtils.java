package com.fy.baselibrary.utils.os;

import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 判断手机系统类型 工具类
 * Created by fangs on 2017/7/4.
 */
public class OSUtils {

    public static final int MIUI = 1000;
    public static final int FLYME = 1001;
    public static final int EMUI = 1002;
    public static final int OTHER = 1003;

    //MIUI标识
    public static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    public static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    public static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    //EMUI标识
    public static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    public static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    public static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    //Flyme标识
    public static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    public static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
    public static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    public static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    public static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";

    /**
     * 获取手机系统 类型
     * @return  ROM类型: MIUI_ROM, FLYME_ROM, EMUI_ROM, OTHER_ROM
     */
    public static int getRomType() {
        int rom_type = OTHER;
        try {
            BuildProperties buildProperties = BuildProperties.newInstance();

            if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE) ||
                    buildProperties.containsKey(KEY_EMUI_API_LEVEL) ||
                    buildProperties.containsKey(KEY_EMUI_CONFIG_HW_SYS_VERSION)) {
                return EMUI;
            }
            if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE) ||
                    buildProperties.containsKey(KEY_MIUI_VERSION_NAME) ||
                    buildProperties.containsKey(KEY_MIUI_INTERNAL_STORAGE)) {
                return MIUI;
            }
            if (buildProperties.containsKey(KEY_FLYME_ICON_FALG) ||
                    buildProperties.containsKey(KEY_FLYME_SETUP_FALG) ||
                    buildProperties.containsKey(KEY_FLYME_PUBLISH_FALG)) {
                return FLYME;
            }
            if (buildProperties.containsKey(KEY_FLYME_ID_FALG_KEY)) {
                String romName = buildProperties.getProperty(KEY_FLYME_ID_FALG_KEY);
                if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
                    return FLYME;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rom_type;
    }

    /**
     * 判断手机是否是小米
     * @return
     */
    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 判断手机是否是魅族
     * @return
     */
    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 是否是 Android 6.0 及以上版本
     */
    public static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是 Android 7.0 及以上版本
     */
    public static boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 是否是 Android 8.0 及以上版本
     */
    public static boolean isAndroid8() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 是否是 Android 10.0 及以上版本
     */
    public static boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 是否是 Android 11.0 及以上版本
     */
    public static boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }
}
