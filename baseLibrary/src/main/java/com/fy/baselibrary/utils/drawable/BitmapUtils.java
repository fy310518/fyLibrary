package com.fy.baselibrary.utils.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.HanziToPinyin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * description bitmap 相关操作
 * Created by fangs on 2020/7/6 16:32.
 */
public class BitmapUtils {

    private BitmapUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 仿钉钉 生成默认的头像 【根据 用户名 和 用户 id】
     * @param userName
     * @param userId
     * @return 返回图片本地路径
     */
    public static String generateDefaultAvatar(String userName, String userId) {
        String string = HanziToPinyin.getSelling(userName);
        String fileName = string + "_" + userId;

        String filePath = FileUtils.folderIsExists(FileUtils.headImg, ConfigUtils.getType()).getPath();
        File file = FileUtils.getTempFile(fileName, filePath);

        String imgFilePath = file.getPath();
        if (!FileUtils.fileIsExist(file.getPath())){ //文件不存在，则创建文件
            Bitmap bitmap = generateDefaultAvatarBitmap(userName, userId);

            imgFilePath = saveBitmap(bitmap, file.getPath());
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            return imgFilePath;
        }

        return imgFilePath;
    }

    /**
     * 根据用户名和用户 id 来生成默认的头像 Bitmap.
     * @param userName 用户名。
     * @param userId   用户id.
     * @return 生成头像的 Bitmap.
     */
    private static Bitmap generateDefaultAvatarBitmap(String userName, String userId) {
        String s = null;

        if (!TextUtils.isEmpty(userName)) {
            if (HanziToPinyin.isChinese(userName)) {
                s = String.valueOf(userName.charAt(userName.length() - 1));
            } else {
                s = String.valueOf(userName.charAt(0));
                if (s.matches("[a-zA-Z]")) {
                    s = s.toUpperCase();
                }
            }
        }

        if (s == null) s = "A";

        String color = getColorRGB(userId);
        return createBitmap(s, color);
    }

    /**
     * 根据字符串 和 颜色 创建 bitmap
     * @param s
     * @param color
     */
    private static Bitmap createBitmap(String s, String color){
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(55);
        paint.setAntiAlias(true);
        int width = 120;
        int height = 120;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor(color));
        Rect rect = new Rect();
        paint.getTextBounds(s, 0, s.length(), rect);
        Paint.FontMetrics fm = paint.getFontMetrics();
        int textLeft = (int) ((width - paint.measureText(s)) / 2);
        int textTop = (int) (height - width / 2 + Math.abs(fm.ascent) / 2 - 6);
        canvas.drawText(s, textLeft, textTop, paint);

        return bitmap;
    }

    /**
     * 将bitmap 保存到文件
     * @param bm
     * @param imgFilePath
     */
    public static String saveBitmap(Bitmap bm, String imgFilePath) {
        File file = FileUtils.fileIsExists(imgFilePath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            // 防止写入异常时，将文件以不完整形式存留，导致缓存永远不正确
            try {
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception ex) {
            }

            return "";
        } catch (Exception e) {
            return "";
        }

        return imgFilePath;
    }


    private static String getColorRGB(String userId) {
        String[] portraitColors = {"#3A91F3", "#74CFDE", "#F14E7D", "#5585A5", "#F9CB4F", "#F56B2F"};
        if (TextUtils.isEmpty(userId)) {
            return portraitColors[0];
        }
        int i = HanziToPinyin.getChsAscii(userId.substring(userId.length() - 1)) % 6;

        return portraitColors[i];
    }

}
