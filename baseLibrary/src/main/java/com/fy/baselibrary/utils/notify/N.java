package com.fy.baselibrary.utils.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.cache.SpfAgent;

import java.io.File;
import java.util.List;

/**
 * description 通知 工具类
 * Created by fangs on 2018/3/18 14:32.
 */
public class N {
    private N() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /** defaults == -100 使用自定义的 铃声和震动*/
    public static final int DEFAULT_CUSTOM = -100;
    /** 通知 PendingIntent 请求码*/
    public static final int requestCode = 235;

    /** app 声音 是否打开 */
    public static final String voiceKEY = "appVoiceKEY";
    /** app 震动 是否打开 */
    public static final String shockKEY = "appShockKEY";


    /**
     * 发送通知
     * @param id   通知的唯一ID
     */
    public static void sendNotify(int id, Notification notification) {
        NotificationManager manager = (NotificationManager) ConfigUtils.getAppCtx().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }

    /**
     * 取消通知
     * @param id      通知的唯一ID
     */
    public static void cancelNotify(int id){
        NotificationManager manager = (NotificationManager) ConfigUtils.getAppCtx().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

    /**
     * 取消所有通知
     */
    public static void cancelAllNotify(){
        NotificationManager manager = (NotificationManager) ConfigUtils.getAppCtx().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    /**
     * 创建通知
     * @param channel
     * @param customBuild
     */
    public static NotificationCompat.Builder createNotifyBuilder(Channel channel, NotifyBuild customBuild) {
        createNotifyChannel(channel);
        Context context = ConfigUtils.getAppCtx();

        //从 SPF 获取 保存的 指定key 的 渠道ID
        String channelID = SpfAgent.init("").getString(channel.channelIdKey);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setWhen(System.currentTimeMillis())
                .setPriority(getLowVersionPriority(channel)) // 通知优先级，优先级确定通知在Android7.1和更低版本上的干扰程度。
                .setVisibility(channel.lockScreenVisibility) // 锁定屏幕公开范围
                .setSmallIcon(customBuild.icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), customBuild.LargeIcon))
                .setColor(ResUtils.getColor(customBuild.iconBgColor))
                .setOnlyAlertOnce(false); // 设置通知是否 只会在通知首次出现时打断用户（通过声音、振动或视觉提示），而之后更新则不会再打断用户。

        if (channel.defaults == N.DEFAULT_CUSTOM) {
            if (null != channel.sound) builder.setSound(channel.sound); //通知 提示音
            if (null != channel.vibrate) builder.setVibrate(channel.vibrate); //通知 震动
            builder.setLights(Color.GREEN, 1000, 2000); //通知栏消息闪灯(亮一秒间隔两秒再亮)
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        } else {
            builder.setDefaults(channel.defaults);
        }

        //判断是否显示自定义通知布局
        if (null == customBuild.remoteViews) {
            builder.setContentTitle(customBuild.title)
                    .setContentText(customBuild.content);
        } else {
            builder.setCustomContentView(customBuild.remoteViews);
        }

        if (null != customBuild.pendingIntent) {
            builder.setContentIntent(customBuild.pendingIntent)//设置通知栏点击意图
                    .setAutoCancel(true);//设置点击通知栏消息后，通知消息自动消失

            if(NotificationManager.IMPORTANCE_HIGH == channel.importance)
                builder.setFullScreenIntent(customBuild.pendingIntent, false);//悬挂式Notification
        }

        return builder;
    }

    /**
     * 管理通知渠道（用户关闭了某个通知渠道，可通过此方法进入系统 通知渠道管理界面，提示用户打开对应的通知渠道）
     * @param act
     * @param channelName
     */
    private static void manageNotifyChannel(Context act, String channelName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager manager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = manager.getNotificationChannel(channelName);

        if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
            Bundle bundle = new Bundle();
            bundle.putString(Settings.EXTRA_APP_PACKAGE, AppUtils.getLocalPackageName());
            bundle.putString(Settings.EXTRA_CHANNEL_ID, channelName);

            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtras(bundle);
            act.startActivity(intent);

            T.showLong("请手动打开通知权限");
        }
    }

    /**
     * 修改指定 ID 的渠道 配置【在 createNotifyBuilder() 方法前调用】
     */
    public static void modifyChannel(Channel channel) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        //从 SPF 获取 保存的 指定key 的 渠道ID
        String channelID = SpfAgent.init("").getString(channel.channelIdKey);
        if (TextUtils.isEmpty(channelID)) return;

        //1、删除 之前的 通知渠道
        NotificationManager notificationManager = (NotificationManager) ConfigUtils.getAppCtx().getSystemService(Context.NOTIFICATION_SERVICE);
        List<NotificationChannel> channelList = notificationManager.getNotificationChannels();
        for (NotificationChannel NotifyChannel : channelList) {
            if (NotifyChannel.getId().contains(channelID)) {
                notificationManager.deleteNotificationChannel(NotifyChannel.getId());
                SpfAgent.init("")
                        .saveString(channel.channelIdKey, System.currentTimeMillis() + "")
                        .commit(false);
                break;
            }
        }

        //2、创建一个新的通知渠道
        createNotifyChannel(channel);
    }

    /**
     * 创建一个 通知渠道
     * @param channel
     */
    public static void createNotifyChannel(Channel channel){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        //从 SPF 获取 保存的 指定key 的 渠道ID
        String channelID = SpfAgent.init("").getString(channel.channelIdKey);
        NotificationChannel NotifyChannel = new NotificationChannel(channelID, channel.channelName, channel.importance);
        NotifyChannel.setDescription(channel.description);

        if (null != channel.sound){
            NotifyChannel.setSound(channel.sound, Notification.AUDIO_ATTRIBUTES_DEFAULT);//设置自定义声音
        } else {
            NotifyChannel.setSound(null, null);//没有声音
        }

        if (null != channel.vibrate){
            // 设置通知出现时的震动（如果 android 设备支持的话）
            NotifyChannel.enableVibration(true);
            NotifyChannel.setVibrationPattern(channel.vibrate);
        } else {
            // 设置通知出现时不震动
            NotifyChannel.enableVibration(false);
            NotifyChannel.setVibrationPattern(new long[]{0});
        }

        NotifyChannel.enableLights(true);//呼吸灯
        NotifyChannel.setLightColor(Color.GREEN);//呼吸灯的灯光颜色

        NotifyChannel.setBypassDnd(true); //设置绕过免打扰模式
        NotifyChannel.setLockscreenVisibility(channel.lockScreenVisibility);//设置在锁屏界面上显示这条通知

        NotifyChannel.setShowBadge(true);//桌面小红点
        NotificationManager notificationManager = (NotificationManager) ConfigUtils.getAppCtx().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(NotifyChannel);
    }

    /**
     * 获取低版本的优先级
     * 要支持搭载 Android 7.1（API 级别 25）或更低版本的设备，
     * 您还必须使用 NotificationCompat 类中的优先级常量针对每条通知调用 setPriority()。
     * @param channel
     */
    private static int getLowVersionPriority(Channel channel) {
        int importance = NotificationCompat.PRIORITY_MIN;
        switch (channel.importance) {
            case NotificationManager.IMPORTANCE_MAX:
                importance = NotificationCompat.PRIORITY_MAX;
                break;
            case NotificationManager.IMPORTANCE_HIGH:
                importance = NotificationCompat.PRIORITY_HIGH;
                break;
            case NotificationManager.IMPORTANCE_DEFAULT:
                importance = NotificationCompat.PRIORITY_DEFAULT;
                break;
            case NotificationManager.IMPORTANCE_LOW:
                importance = NotificationCompat.PRIORITY_LOW;
                break;
            case NotificationManager.IMPORTANCE_MIN:
                importance = NotificationCompat.PRIORITY_MIN;
                break;
        }

        return importance;
    }

    public static class NotifyBuild {
        /** 通知 图标（Android从5.0以上 通知 icon 只使用alpha图层来进行绘制，而不应该包括RGB图层） */
        private int icon;
        /** 通知图标 背景颜色 */
        private int iconBgColor;
        /** 通知 大图标 */
        private int LargeIcon;

        /** 通知标题 */
        private String title;
        /** 通知内容 */
        private String content;

        /** 自定义布局  */
        private RemoteViews remoteViews;

        private PendingIntent pendingIntent;

        public static NotifyBuild init() {
            NotifyBuild build = new NotifyBuild();
            return build;
        }

        public NotifyBuild setIcon(@DrawableRes int icon, @ColorRes int iconBgColor, @DrawableRes int LargeIcon) {
            this.icon = icon;
            this.iconBgColor = iconBgColor;
            this.LargeIcon = LargeIcon;
            return this;
        }

        public NotifyBuild setData(String title, String content) {
            this.title = title;
            this.content = content;
            return this;
        }

        public NotifyBuild setLayout(RemoteViews remoteViews) {
            this.remoteViews = remoteViews;
            return this;
        }

        /**
         * 设置 通知点击 跳转事件
         */
        public NotifyBuild setPendingIntent(Context context, @NonNull Class actClass, Bundle bundle) {
            Intent intent = new Intent(context, actClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (null != bundle)intent.putExtras(bundle);
            this.pendingIntent = PendingIntent.getActivity(context, N.requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return this;
        }

        /**
         * 设置 通知点击 跳转事件
         * @param intents
         */
        public NotifyBuild setPendingIntent(Context context, @NonNull Intent[] intents) {
            this.pendingIntent = PendingIntent.getActivities(context, N.requestCode, intents, PendingIntent.FLAG_UPDATE_CURRENT);
            return this;
        }
    }


    /**
     * 通知渠道 对象
     */
    public static class Channel {
        /** 通知渠道ID key 【android 8.0 渠道概念的 渠道ID 是String类型】*/
        private String channelIdKey;
        /** 通知渠道名称 */
        private String channelName;
        /** 描述 */
        private String description;

        /** 重要性级别 【1 - 5】*/
        private int importance = NotificationManager.IMPORTANCE_DEFAULT;
        /** 锁定屏幕公开范围 */
        private int lockScreenVisibility = NotificationCompat.VISIBILITY_SECRET;

        /**
         * 通知提示模式
         * NotificationCompat.DEFAULT_SOUND	        添加默认声音提醒
         * NotificationCompat.DEFAULT_VIBRATE	    添加默认震动提醒
         * NotificationCompat.DEFAULT_LIGHTS	    添加默认呼吸灯提醒
         * NotificationCompat.DEFAULT_ALL	        同时添加以上三种默认提醒
         * NotificationCompat.FLAG_ONLY_ALERT_ONCE	静默
         * N.DEFAULT_CUSTOM            使用自定义的 铃声和震动
         */
        private int defaults = NotificationCompat.FLAG_ONLY_ALERT_ONCE;

        /** 震动模式 */
        private long[] vibrate = new long[]{10, 1000, 1000};
        /** 通知消息提示音 Uri (默认：系统铃声管理器中的提示音效) */
        private Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        public static Channel init() {
            return new Channel();
        }


        /** 以下为构建参数 */
        public Channel setChannel(String channelIdKey, String channelName, String description) {
            this.channelIdKey = channelIdKey;
            this.channelName = channelName;
            this.description = description;
            return this;
        }

        public Channel setImportance(int importance) {
            this.importance = importance;
            return this;
        }

        public Channel setDefaults(int defaults) {
            this.defaults = defaults;
            return this;
        }

        public Channel setLockScreenVisibility(int lockScreenVisibility) {
            this.lockScreenVisibility = lockScreenVisibility;
            return this;
        }

        public Channel setVibrate(long[] vibrate) {
            this.vibrate = vibrate;
            return this;
        }

        /** 设置使用 系统铃声管理器中的提示音效 */
        public Channel setSound(int type){
            this.sound = RingtoneManager.getDefaultUri(type);
            return this;
        }

        /** 设置 文件管理器中的 提示音效 */
        public Channel setSoundFilePath(String soundFilePath) {
            this.sound = Uri.fromFile(new File(soundFilePath));
            return this;
        }

        /** 设置 raw 资源中的 提示音效 */
        public Channel setSoundFilePath(@RawRes int soundFilePath) {
            this.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + AppUtils.getLocalPackageName() + "/raw/" + soundFilePath);
            return this;
        }
    }


}
