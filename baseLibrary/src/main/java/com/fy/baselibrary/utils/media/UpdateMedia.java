package com.fy.baselibrary.utils.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            insertImageFileIntoMediaStore(file.getName(), file.getPath());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            new MediaScanner(context).scanFile(file, null);
        } else {
            scanFolder(context, action, file);
        }
    }

    /**
     * AndroidQ以上保存图片到公共目录
     * @param imageName 图片名称
     * @param relativePath 缓存路径
     */
    private static Uri insertImageFileIntoMediaStore (String imageName, String relativePath) {
        if (TextUtils.isEmpty(relativePath)) return null;

        Uri insertUri = null;
        ContentResolver resolver = ConfigUtils.getAppCtx().getContentResolver();
        //设置文件参数到ContentValues中
        ContentValues values = new ContentValues();
        //设置文件名
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        //设置文件描述，这里以文件名代替
        values.put(MediaStore.Images.Media.DESCRIPTION, imageName);
        //设置文件类型为image/*
        values.put(MediaStore.Images.Media.MIME_TYPE, getMimeType(imageName));
        //注意：MediaStore.Images.Media.RELATIVE_PATH需要targetSdkVersion=29,
        //故该方法只可在Android10的手机上执行
        values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath);
        //EXTERNAL_CONTENT_URI代表外部存储器
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //insertUri表示文件保存的uri路径
        insertUri = resolver.insert(external, values);
        return insertUri;
    }

    /**
     *根据文件名 获取 mimeType
     * @param fileName
     */
    public static String getMimeType(String fileName){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileName);
        return type;
    }

}
