package com.fy.baselibrary.utils.notify;

import android.annotation.TargetApi;
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
import android.widget.RemoteViews;

import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.ResUtils;

import java.io.File;

/**
 * 简单通知 工具类
 * Created by fangs on 2018/3/18.
 */
public class NotifyUtils {

    private NotifyUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 创建一个通知渠道(此方法一般在 发送通知消息前执行, 或者在 启动页 等待的时间执行)
     * @param ctx
     * @param channelId     渠道id （创建通知渠道的 channelId 和 发送通知时候的渠道名称 channelName 一致）
     * @param channelName   渠道名称
     * @param importance    渠道重要级别 (NotificationManager.IMPORTANCE_DEFAULT)
     * @param isVibrate     是否震动
     * @param hasSound      是否有声音
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void createNotificationChannel(Context ctx, String channelId, String channelName, int importance,
                                                 boolean isVibrate, boolean hasSound, Uri sound) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            if (null != sound){
                // 设置通知出现时的震动（如果 android 设备支持的话）
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{10, 1000, 1000});

                channel.setSound(sound, Notification.AUDIO_ATTRIBUTES_DEFAULT);//设置自定义声音
            } else {
                if (isVibrate) {
                    // 设置通知出现时的震动（如果 android 设备支持的话）
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{10, 1000, 1000});
                } else {
                    // 设置通知出现时不震动
                    channel.enableVibration(false);
                    channel.setVibrationPattern(new long[]{0});
                }

                if (!hasSound) channel.setSound(null, null);//没有声音
            }


            channel.enableLights(true);//呼吸灯
            channel.setLightColor(Color.GREEN);//呼吸灯的灯光颜色

            channel.setBypassDnd(true); //设置绕过免打扰模式
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//设置在锁屏界面上显示这条通知

            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 管理通知渠道（用户关闭了某个通知渠道，可通过此方法进入系统 通知渠道管理界面，提示用户打开对应的通知渠道）
     * @param act
     * @param channelName
     */
    private static void manageNotificationChannel(Context act, String channelName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = manager.getNotificationChannel(channelName);

            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Bundle bundle = new Bundle();
                bundle.putString(Settings.EXTRA_APP_PACKAGE, AppUtils.getLocalPackageName());
                bundle.putString(Settings.EXTRA_CHANNEL_ID, channelName);

                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtras(bundle);
                act.startActivity(intent);

                T.showLong("请手动将通知打开");
            }
        }
    }

    /**
     * 配置 NotificationCompat.Builder
     * @param act
     * @param fyBuild        通知关键数据 包装类
     */
    public static NotificationCompat.Builder createNotifyBuilder(Context act, FyBuild fyBuild) {
        manageNotificationChannel(act, fyBuild.channelName);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(act, fyBuild.channelName)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(fyBuild.icon)
                .setLargeIcon(BitmapFactory.decodeResource(act.getResources(), fyBuild.icon))
                .setColor(ResUtils.getColor(fyBuild.iconBgColor))
                .setPriority(fyBuild.priority)//设置通知消息优先级
                .setAutoCancel(true);//设置点击通知栏消息后，通知消息自动消失


        if (fyBuild.defaults == AppNotifyUtils.DEFAULT_CUSTOM) {
            builder.setSound(fyBuild.sound) //通知栏消息提示音
                    .setVibrate(new long[]{10, 1000, 1000}) //通知栏消息震动
                    .setLights(Color.GREEN, 1000, 2000); //通知栏消息闪灯(亮一秒间隔两秒再亮)
        } else {
            builder.setDefaults(fyBuild.defaults);
        }

        //判断是否显示自定义通知布局
        if (null == fyBuild.remoteViews) {
            builder.setContentTitle(fyBuild.msgTitle)
                    .setContentText(fyBuild.msgContent);
        } else {
            builder.setCustomContentView(fyBuild.remoteViews);
        }

        if (null != fyBuild.pendingIntent) {
            builder.setContentIntent(fyBuild.pendingIntent); //设置通知栏点击意图

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                // requestCode是0的时候三星手机点击通知栏通知不起作用
                // 关联PendingIntent
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            }
        }

        return builder;
    }


    public static class FyBuild {
        /** 通知渠道 Id */
        private int channelId;
        /** 通知渠道名称 */
        private String channelName;
        /** 通知消息提示音 Uri (默认：系统铃声管理器中的提示音效) */
        private Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        /**
         * 通知提示模式
         * NotificationCompat.DEFAULT_SOUND	        添加默认声音提醒
         * NotificationCompat.DEFAULT_VIBRATE	    添加默认震动提醒
         * NotificationCompat.DEFAULT_LIGHTS	    添加默认呼吸灯提醒
         * NotificationCompat.DEFAULT_ALL	        同时添加以上三种默认提醒
         * NotificationCompat.FLAG_ONLY_ALERT_ONCE	静默
         * AppNotifyUtils.DEFAULT_CUSTOM            使用自定义的 铃声和震动
         */
        private int defaults = NotificationCompat.FLAG_ONLY_ALERT_ONCE;

        /**
         * 设置通知重要性级别
         * Android 8.0以及上，在 NotificationChannel 的构造函数中指定，总共要五个级别；
         * Android 7.1（API 25）及以下的设备，还得调用NotificationCompat 的 setPriority方法来设置
         *
         * Notification.PRIORITY_MAX	重要而紧急的通知，通知用户这个事件是时间上紧迫的或者需要立即处理的。
         * Notification.PRIORITY_HIGH	高优先级用于重要的通信内容，例如短消息或者聊天，这些都是对用户来说比较有兴趣的
         * Notification.PRIORITY_DEFAULT	默认优先级用于没有特殊优先级分类的通知
         * Notification.PRIORITY_LOW	低优先级可以通知用户但又不是很紧急的事件。只显示状态栏图标
         * Notification.PRIORITY_MIN	用于后台消息 (例如天气或者位置信息)。只有用户下拉通知抽屉才能看到内容
         */
        private int priority = NotificationCompat.PRIORITY_DEFAULT;

        /** 通知 图标（Android从5.0以上 通知 icon 只使用alpha图层来进行绘制，而不应该包括RGB图层） */
        private int icon;
        /** 通知图标 背景颜色 */
        private int iconBgColor;
        /** 通知标题 */
        private String msgTitle;
        /** 通知内容 */
        private String msgContent;

        /** 自定义布局  */
        private RemoteViews remoteViews;

        PendingIntent pendingIntent;

        private NotificationCompat.Builder mBuilder;
        private NotificationManager manager;

        public static FyBuild init() {
            return new FyBuild();
        }

        /** 发送一次通知 */
        public void sendNotify(){
            assert mBuilder != null;
            Notification notification = mBuilder.build();
            notification.contentIntent = this.pendingIntent;
            assert manager != null;
            manager.notify(channelId, notification);
        }

        /** 取消通知 */
        public void dismissNotify(){
            assert mBuilder != null;
            manager.cancel(channelId);
        }

        /** 以下为构建参数 */
        public FyBuild setChannel(int channelId, String channelName) {
            this.channelId = channelId;
            this.channelName = channelName;
            return this;
        }

        public FyBuild setDefaults(int defaults) {
            this.defaults = defaults;
            return this;
        }

        /** 设置 文件管理器中的 提示音效 */
        public FyBuild setSoundFilePath(String soundFilePath) {
            this.sound = Uri.fromFile(new File(soundFilePath));
            return this;
        }

        /** 设置 raw 资源中的 提示音效 */
        public FyBuild setSoundFilePath(@RawRes int soundFilePath) {
            this.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + AppUtils.getLocalPackageName() + "/" + soundFilePath);
            return this;
        }

        public FyBuild setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public FyBuild setIcon(@DrawableRes int icon, @ColorRes int iconBgColor) {
            this.icon = icon;
            this.iconBgColor = iconBgColor;
            return this;
        }

        public FyBuild setMsgTitle(String msgTitle) {
            this.msgTitle = msgTitle;
            return this;
        }

        public FyBuild setMsgContent(String msgContent) {
            this.msgContent = msgContent;
            return this;
        }

        public FyBuild setLayout(RemoteViews remoteViews) {
            this.remoteViews = remoteViews;
            return this;
        }

        public NotificationCompat.Builder getFBuilder() {
            return mBuilder;
        }

        /**
         * 修改 知道渠道 id的 渠道设置【在 createManager() 方法前调用】
         */
        public FyBuild modifyChannel(Context act) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return this;
            if (defaults == AppNotifyUtils.DEFAULT_CUSTOM) {
                channelName = AppNotifyUtils.initNotificationChannel(act, channelId + "", true,
                        channelId + "", channelName, sound);
            } else {
                channelName = AppNotifyUtils.initNotificationChannel(act, channelId + "", true,
                        channelId + "", channelName, null);
            }
            return this;
        }

        /**
         * 最后构建 NotificationCompat.Builder 和 NotificationManager
         * @param act
         */
        public FyBuild createManager(Context act) {
            mBuilder = NotifyUtils.createNotifyBuilder(act, this);
            manager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
            return this;
        }

        /**
         * 最后构建 NotificationCompat.Builder 和 NotificationManager
         * @param act
         * @param actClass
         * @param bundle
         */
        public FyBuild createManager(Context act, @NonNull Class actClass, Bundle bundle) {
            Intent resultIntent = new Intent(act, actClass);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (null != bundle)resultIntent.putExtras(bundle);
            this.pendingIntent = PendingIntent.getActivity(act, AppNotifyUtils.requestCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            createManager(act);
            return this;
        }

    }
}
