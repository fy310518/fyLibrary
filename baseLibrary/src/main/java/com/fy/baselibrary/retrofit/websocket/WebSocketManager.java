package com.fy.baselibrary.retrofit.websocket;

import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.utils.notify.L;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * DESCRIPTION：webSocket 管理类
 * Created by fangs on 2019/10/19 17:36.
 */
public class WebSocketManager {
    private final static String TAG = "WebSocketManager";

    private final static int MAX_NUM = 100;       // 最大重连数
    private final static int MILLIS = 5000;     // 重连间隔时间，毫秒
    private int connectNum = 0;//记录 重连次数
    private boolean isConnect = false;//是否连接成功

    private volatile static WebSocketManager manager;

    private OkHttpClient client;
    private Request request;
    private IReceiveMessage receiveMessage;
    private WebSocket mWebSocket;

    private WebSocketManager() {
    }

    public static WebSocketManager getInstance() {
        if (manager == null) {
            synchronized (WebSocketManager.class) {
                if (manager == null) {
                    manager = new WebSocketManager();
                }
            }
        }
        return manager;
    }

    public void init(String url, IReceiveMessage message) {
        client = RequestUtils.getOkBuilder()
                .pingInterval(59, TimeUnit.SECONDS) // 设置 PING 帧发送间隔;
                .build();

        request = new Request.Builder().url(url).build();
        receiveMessage = message;
        connect();
    }

    /**
     * 连接
     */
    public void connect() {
        if (isConnect()) {
            L.i(TAG, "web socket connected");
            return;
        }
        client.newWebSocket(request, createListener());
    }

    /**
     * 重连
     */
    public void reconnect() {
        if (connectNum <= MAX_NUM) {
            try {
                Thread.sleep(connectNum * MILLIS);
                connect();
                connectNum++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            L.i(TAG, "reconnect over " + MAX_NUM + ",please check url or network");
        }
    }

    /**
     * 是否连接
     */
    public boolean isConnect() {
        return mWebSocket != null && isConnect;
    }

    /**
     * 发送消息
     *
     * @param text 字符串
     * @return boolean
     */
    public boolean sendMessage(String text) {
        if (!isConnect()) return false;
        return mWebSocket.send(text);
    }

    /**
     * 发送消息
     *
     * @param byteString 字符集
     * @return boolean
     */
    public boolean sendMessage(ByteString byteString) {
        if (!isConnect()) return false;
        return mWebSocket.send(byteString);
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (isConnect()) {
            mWebSocket.close(1001, "客户端主动关闭连接");
        }
    }

    private WebSocketListener createListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                L.e(TAG, "open:" + response.toString());

                mWebSocket = webSocket;
                isConnect = response.code() == 101;
                if (!isConnect) {
                    reconnect();
                } else {
                    connectNum = 0;
                    L.e(TAG, "connect success.");
                    if (receiveMessage != null) {
                        receiveMessage.onConnectSuccess();
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if (receiveMessage != null) {
                    receiveMessage.onMessage(text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (receiveMessage != null) {
                    receiveMessage.onMessage(bytes.base64());
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);

                isConnect = false;
                if (receiveMessage != null) {
                    receiveMessage.onClose();
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);

                connectNum = 0;
                isConnect = false;
                if (receiveMessage != null) receiveMessage.onClose();

                if (null != mWebSocket) mWebSocket = null;
                if (null != manager) manager = null;
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                L.e(TAG, "connect failed throwable：" + t.getMessage());
                if (response != null) L.e(TAG, "connect failed：" + response.message());

                if (receiveMessage != null) receiveMessage.onConnectFailed();

                isConnect = false;
                reconnect();
            }
        };
    }

}
