package com.fy.baselibrary.permission;

import java.util.List;

/**
 * 权限管理 回调接口
 * Created by fangs on 2018/8/27 15:36.
 */
public interface OnPermission {

    /**
     * 有权限被授予时回调（部分或全部授予）
     *
     * @param denyList    请求失败的权限组
     * @param isAll       是否全部授予了
     */
    void hasPermission(List<String> denyList, boolean isAll);

    /**
     * 权限被全部拒绝时回调
     *
     * @param denyList 请求失败的权限组
     */
    void noPermission(List<String> denyList);


//    Android 的权限大致分为三种：
//    普通权限：只需要在清单文件中注册即可
//    危险权限：需要在代码中动态申请，以弹系统 Dialog 的形式进行请求
//    特殊权限：需要在代码中动态申请，以跳系统 Activity 的形式进行请求
    /** 收集Android 各版本 特殊权限 */
    /** 外部存储权限（特殊权限，需要 Android 11 及以上） */
    String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";
    /** 应用安装权限（特殊权限，需要 Android 8.0 及以上） */
    String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";
    /** 通知栏权限（特殊权限，需要 Android 7.0 及以上） */
    String NOTIFICATION_SERVICE = "android.permission.ACCESS_NOTIFICATION_POLICY";
    /** 悬浮窗权限（特殊权限，需要 Android 6.0 及以上） */
    String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";
    /** 系统设置权限（特殊权限，需要 Android 6.0 及以上） */
    String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";


    /** 在后台获取位置（需要 Android 10.0 及以上） */
    String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    /** 读取照片中的地理位置（需要 Android 10.0 及以上）*/
    String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";
    /** 使用传感器 */
    String BODY_SENSORS = "android.permission.BODY_SENSORS";
    /** 获取活动步数（需要 Android 10.0 及以上） */
    String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    /**
     * 处理拨出电话
     * @deprecated  在 Android 10 已经废弃，请直接使用 {link ANSWER_PHONE_CALLS}
     */
    String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";
    /** 接听电话（需要 Android 8.0 及以上） */
    String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";
    /** 读取手机号码（需要 Android 8.0 及以上） */
    String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /** 读取电话状态 */
    String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";


}
