package com.fy.baselibrary.utils.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.DensityUtils;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.HanziToPinyin;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
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
    public static String generateDefaultAvatar(@NonNull String userName, @NonNull String userId) {
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
     * 根据字符串 和 颜色 创建 默认的头像 bitmap
     * @param s
     * @param color
     */
    private static Bitmap createBitmap(String s, String color){
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(55);
        paint.setAntiAlias(true);
        int width = 127;
        int height = 127;
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

    public static String saveWaterMar(String userName, String suffix, Bitmap.Config config){
        String fileName = userName + "_" + suffix;
        String filePath = FileUtils.folderIsExists(FileUtils.headImg, ConfigUtils.getType()).getPath();
        File file = FileUtils.getTempFile(fileName, filePath);

        String imgFilePath = file.getPath();
        if (!FileUtils.fileIsExist(file.getPath())){ //文件不存在，则创建文件
            Bitmap bitmap = createWaterMarkBitmap(userName, suffix, config);

            imgFilePath = saveBitmap(bitmap, file.getPath());
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            return imgFilePath;
        }

        return imgFilePath;
    }

    /**
     * 根据用户名和用户 suffix【手机号 或 工号】 创建水印 bitmap
     * @param userName
     * @param suffix
     */
    public static Bitmap createWaterMarkBitmap(@NonNull String userName, @NonNull String suffix, @NonNull Bitmap.Config config) {
        final int mPaddingRight = DensityUtils.dp2px(100);
        final int mPaddingBottom = DensityUtils.dp2px(60);
        final int ROTATION = 20;
        final int TEXT_SIZE = 30;
        String mContent = userName + " " + suffix;

        TextPaint paint = new TextPaint();
        paint.setColor(ResUtils.getColor(R.color.waterMarkContent));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(TEXT_SIZE);

        Rect rect = new Rect();
        paint.getTextBounds(mContent, 0, mContent.length(), rect);

        int textWidth = rect.width();
        int textHeight = rect.height();

        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();

        Bitmap mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, config);

        Canvas canvas = new Canvas(mBitmap);
        canvas.drawColor(ResUtils.getColor(R.color.waterMarkBg));

        double sinRotateAngle = Math.sin(ROTATION);
        double cosRotateAngle = Math.cos(ROTATION);
        int canvasHeight = (int) ((screenHeight * cosRotateAngle + screenWidth * sinRotateAngle) * 1.5);
        int canvasWidth = (int) (screenHeight * sinRotateAngle + screenWidth * cosRotateAngle);

        canvas.rotate(-ROTATION, screenWidth / 2, screenHeight / 2);

        int startX = 0;
        int startY = 0;
        int i = 1;
        for (int positionY = startY; positionY <= canvasHeight; positionY += (textHeight + mPaddingBottom)) {
            i++;
            if (i % 2 == 0) {
                startX = -screenWidth + +textWidth + (mPaddingRight - textWidth) / 2;
            } else {
                startX = -screenWidth;
            }
            for (float positionX = startX; positionX <= canvasWidth; positionX += (textWidth + mPaddingRight)) {
                canvas.drawText(mContent, positionX, positionY, paint);
            }
        }

        canvas.save();
        canvas.restore();

        return mBitmap;
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
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
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

    /**
     * 根据 userId 最后一个字符 的 ascii 获取颜色
     * 应用层 可以 重新定义 imgHeadBgColor 颜色数组 实现自定义，背景色
     */
    private static String getColorRGB(String userId) {
        String[] portraitColors = ResUtils.getStrArray(R.array.imgHeadBgColor);
        if (TextUtils.isEmpty(userId)) {
            return portraitColors[0];
        }
        int i = HanziToPinyin.getChsAscii(userId.substring(userId.length() - 1)) % portraitColors.length;

        return portraitColors[i];
    }

    /**
     * sd卡 文件 转 drawable
     * @param path
     * @return
     * @throws IOException
     */
    public static Drawable stringToDrawable(String path) {
        Drawable drawable = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            //先转换成bitmap
            Bitmap bmp = BitmapFactory.decodeStream(input);

            //再转换成drawable
            drawable = new BitmapDrawable(ConfigUtils.getAppCtx().getResources(), bmp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return drawable;
    }

    /**
     * drawable 转 bitmap
     * @param drawable
     */
    public static Bitmap drawToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

}
