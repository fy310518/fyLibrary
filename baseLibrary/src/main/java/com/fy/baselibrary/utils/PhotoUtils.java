package com.fy.baselibrary.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.media.UpdateMedia;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Android 调用系统相机 相册 裁剪 相关方法 工具类
 * Created by fangs on 2018/1/12.
 */
public class PhotoUtils {

    private PhotoUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 调用系统相机 拍照(监听返回数据时候不要 判断 data是否为空)
     *
     * @param activity      当前activity
     * @param takeImageFile 拍照成功后的图片文件
     * @return Intent 为了方便 activity 或者 fragment中启动拍照
     */
    public static Intent takePicture(Activity activity, File takeImageFile) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {

            if (null != takeImageFile) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！

                Uri uri;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    uri = Uri.fromFile(takeImageFile);
                } else {

                    /**
                     * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为 FileProvider
                     * 并且这样可以解决MIUI系统上拍照返回size为0的情况
                     */
                    uri = FileProvider.getUriForFile(activity, AppUtils.getFileProviderName(), takeImageFile);
                    //加入uri权限 要不三星手机不能拍照
                    List<ResolveInfo> resInfoList = activity.getPackageManager()
                            .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, uri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                L.e("PhotoUtils", AppUtils.getFileProviderName());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }

        return takePictureIntent;
//        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * 调用（打开）系统相册
     *
     * @param activity    当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openPic(Activity activity, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }

    /**
     * 调用系统裁剪  安卓7.0以下
     *
     * @param activity    当前activity
     * @param filePath     剪裁原图的 路径
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Activity activity, String filePath, Uri desUri, int aspectX,
                                    int aspectY, int width, int height, int requestCode) {

        //剪裁原图的Uri
        Uri orgUri = PhotoUtils.getUri(activity, new File(filePath));

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取 图片裁剪 Uri
     */
    public static Uri getUri(Context context, File file){
        Uri sourceUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sourceUri = PhotoUtils.getImageContentUri(context, file);
        } else {
            sourceUri = Uri.fromFile(file);
        }

        return sourceUri;
    }

    /**
     * 获取文件的Content uri路径 安卓7.0获取setDataAndType裁剪后的照片
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 把图片保存到指定的文件夹
     * @param bmp
     */
    public static String saveImageToGallery(Bitmap bmp) {
        File file = FileUtils.createFile(FileUtils.IMG, "IMG_", ".jpg", 0);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getPath();
    }


    /**
     * 读取uri所在的图片
     *
     * @param uri      图片对应的Uri
     * @return 获取图像的Bitmap
     */
    public static Bitmap getBitmapFromUri(Uri uri) {
        Context mContext = ConfigUtils.getAppCtx();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取图片的旋转角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param path 需要旋转的图片路径
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static String rotateBitmapByDegree(String path, int degree) {
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeFile(path, getBitmapOption(1)); //将图片的长和宽缩小味原来的1/2
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        String targetPath = saveImageToGallery(newBitmap);

        if (!bitmap.isRecycled()) bitmap.recycle();
        if (!newBitmap.isRecycled()) newBitmap.recycle();

        bitmap = null;
        newBitmap = null;

        return targetPath;
    }


    private static BitmapFactory.Options getBitmapOption(int inSampleSize){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }
}
