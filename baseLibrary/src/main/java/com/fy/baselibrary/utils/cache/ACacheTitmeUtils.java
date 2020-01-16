package com.fy.baselibrary.utils.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

/**
 * 缓存时间计算工具类
 * Created by 下载 on 2017/4/24.
 */
public class ACacheTitmeUtils {

    /**
     * 判断缓存的String数据是否到期
     *
     * @param str
     * @return true：到期了 false：还没有到期
     */
    protected static boolean isDue(String str) {
        return isDue(str.getBytes());
    }

    /**
     * 判断缓存的byte数据是否到期
     *
     * @param data
     * @return true：到期了 false：还没有到期
     */
    protected static boolean isDue(byte[] data) {
        String[] strs = getDateInfoFromDate(data);
        if (strs != null && strs.length == 2) {
            String saveTimeStr = strs[0];
            while (saveTimeStr.startsWith("0")) {
                saveTimeStr = saveTimeStr
                        .substring(1, saveTimeStr.length());
            }
            long saveTime = Long.valueOf(saveTimeStr);
            long deleteAfter = Long.valueOf(strs[1]);
            if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                return true;
            }
        }
        return false;
    }

    protected static String newStringWithDateInfo(int second, String strInfo) {
        return createDateInfo(second) + strInfo;
    }

    protected static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
        byte[] data1 = createDateInfo(second).getBytes();
        byte[] retdata = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, retdata, 0, data1.length);
        System.arraycopy(data2, 0, retdata, data1.length, data2.length);
        return retdata;
    }

    protected static String clearDateInfo(String strInfo) {
        if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
            strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1,
                    strInfo.length());
        }
        return strInfo;
    }

    protected static byte[] clearDateInfo(byte[] data) {
        if (hasDateInfo(data)) {
            return copyOfRange(data, indexOf(data, mSeparator) + 1,
                    data.length);
        }
        return data;
    }

    private static boolean hasDateInfo(byte[] data) {
        return data != null && data.length > 15 && data[13] == '-'
                && indexOf(data, mSeparator) > 14;
    }

    private static String[] getDateInfoFromDate(byte[] data) {
        if (hasDateInfo(data)) {
            String saveDate = new String(copyOfRange(data, 0, 13));
            String deleteAfter = new String(copyOfRange(data, 14,
                    indexOf(data, mSeparator)));
            return new String[]{saveDate, deleteAfter};
        }
        return null;
    }

    private static int indexOf(byte[] data, char c) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }

    private static final char mSeparator = ' ';

    private static String createDateInfo(int second) {
        String currentTime = System.currentTimeMillis() + "";
        while (currentTime.length() < 13) {
            currentTime = "0" + currentTime;
        }
        return currentTime + "-" + second + mSeparator;
    }

    /*
     * Bitmap → byte[]
     */
    protected static byte[] Bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /*
     * byte[] → Bitmap
     */
    protected static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /*
     * Drawable → Bitmap
     */
    protected static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
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

    /*
     * Bitmap → Drawable
     */
    @SuppressWarnings("deprecation")
    protected static Drawable bitmap2Drawable(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        return new BitmapDrawable(bm);
    }
}
