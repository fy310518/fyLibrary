package com.fy.baselibrary.utils.media;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;

import com.fy.baselibrary.utils.AppUtils;

import java.io.File;

/**
 * describe：Uri 获取工具类
 * Created by fangs on 2020/1/13 0013 上午 10:34.
 */
public class UriUtils {

    private UriUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取 指定 文件Uri
     * @param context
     * @param file
     */
    public static Uri fileToUri(Context context, File file){
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(context, AppUtils.getFileProviderName(), file);
        }

        return uri;
    }

    /**
     * 获取资源 Uri
     * @param context
     * @param resId
     */
    public static Uri getResUri(Context context, int resId){
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
    }
}
