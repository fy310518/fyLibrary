package com.fy.baselibrary.utils.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.cache.SpfAgent;

import java.util.List;

/**
 * DESCRIPTION：应用层 通知 工具类
 * Created by fangs on 2019/4/24 16:59.
 */
public class AppNotifyUtils {

    /** defaults == -100 使用自定义的 铃声和震动*/
    public static final int DEFAULT_CUSTOM = -100;
    /** 通知 PendingIntent 请求码*/
    public static final int requestCode = 235;


    /**
     * app 声音 是否打开
     */
    public static final String voiceKEY = "appVoiceKEY";
    /**
     * app 震动 是否打开
     */
    public static final String shockKEY = "appShockKEY";


    /**
     * 缓存的 通知渠道 key(一个项目中 可能有多个)
     */
    public static final String chatNumKEY = "chatNumKEY";
    //聊天 渠道 id 前缀
    public static final String channelId = "chat";
    //聊天 渠道 名称
    public static final String channelName = "聊天消息";

    //webSocket 通知 渠道 Id
    public static final int webSocketID = 4539;
    public static final String channelKEY1 = "WebSocketKEY1";
    //webSocket 渠道 id 前缀
    public static final String webSocketId = "WebSocket";
    //webSocket 渠道 名称
    public static final String webSocketName = "WebSocket消息";

    //应用更新 通知 渠道 Id
    public static final int appUpdateID = 4538;


    /**
     * 初始化 一个 通知渠道 OR 更新一个通知渠道
     *
     * @param ctx
     * @param chatNumKEY
     * @param isDelete    是否删除 之前的 通知渠道
     * @param channelId   渠道id 前缀
     * @param channelName 渠道名称
     * return String 渠道id
     */
    public static String initNotificationChannel(Context ctx, String chatNumKEY, boolean isDelete,
                                               String channelId, String channelName, Uri sound) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return "";

        if (isDelete) {
            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            List<NotificationChannel> channelList = notificationManager.getNotificationChannels();
            for (NotificationChannel channel : channelList) {
                if (channel.getId().contains(channelId)) {
                    notificationManager.deleteNotificationChannel(channel.getId());
                    SpfAgent.init("")
                            .saveLong(chatNumKEY, System.currentTimeMillis())
                            .commit(false);
                    break;
                }
            }
        }


        boolean shock = SpfAgent.init("").getBoolean(AppNotifyUtils.shockKEY, true);
        boolean voice = SpfAgent.init("").getBoolean(AppNotifyUtils.voiceKEY, true);
        int importance = NotificationManager.IMPORTANCE_LOW;
        if ((voice && !shock) || (!voice && shock)){
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        } else if (voice && shock){
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        long channelNum = SpfAgent.init("").getLong(chatNumKEY);
        String channelIdStr = channelId + channelNum;

        NotifyUtils.createNotificationChannel(ctx, channelIdStr, channelName, importance,
                shock, voice, sound);

        return channelIdStr;
    }


}
