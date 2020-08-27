package com.fy.baselibrary.retrofit.websocket;

/**
 * DESCRIPTION：webSocket 回调接口
 * Created by fangs on 2019/10/19 17:28.
 */
public interface IReceiveMessage {

    void onConnectSuccess();// 连接成功

    void onConnectFailed();// 连接失败

    void onClose(); // 关闭

    void onMessage(String text);//接收消息

}
