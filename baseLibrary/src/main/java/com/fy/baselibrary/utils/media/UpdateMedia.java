package com.fy.baselibrary.utils.media;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;

/**
 * 更新媒体库 （4.4之后 使用）
 * 调用方式：new UpdateMedia(fileName).runUpdate();
 * Created by fangs on 2018/1/31.
 */
public class UpdateMedia implements MediaScannerConnection.MediaScannerConnectionClient {

    public static final String TAG = "UpdateMedia";

    MediaScannerConnection mediaScanConn;
    String filePath = "";//需要扫描的文件 路径

    public UpdateMedia(String filePath) {
        this.filePath = filePath;
        this.mediaScanConn = new MediaScannerConnection(ConfigUtils.getAppCtx(), this);
    }

    @Override
    public void onMediaScannerConnected() {
        L.e(TAG, "扫描");
        /** 这个方法一次只能扫描一个文件，path 必须是一个具体的文件，不能是目录 */
        mediaScanConn.scanFile(filePath, "");
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        //当client和MediaScaner扫描完成后  进行关闭我们的连接
        L.e(TAG, "扫描完成");
        mediaScanConn.disconnect();
    }

    /**
     * 通知媒体库更新
     */
    public void runUpdate() {
        mediaScanConn.connect();
    }



////////////////////////////////// 以下为静态方法 ///////////////////////////////////////////

    /**
     * 通知系统媒体库更新 (使用此 广播方式)
     * @param context
     * @param action    扫描的类型 文件或目录（文件：Intent.ACTION_MEDIA_SCANNER_SCAN_FILE）
     * @param file      要扫描的文件
     */
    public static void scanFolder(Context context, String action, File file) {
        Intent mediaScanIntent = new Intent(action);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 通知系统媒体库更新
     * @param context
     * @param action    扫描的类型 文件或目录
     * @param file      要扫描的文件或目录
     */
    public static void scanMedia(Context context, String action, File file){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            new MediaScanner(context).scanFile(file, null);
        } else {
            scanFolder(context, action, file);
        }
    }
}
