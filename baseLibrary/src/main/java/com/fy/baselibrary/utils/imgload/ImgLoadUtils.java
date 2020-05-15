package com.fy.baselibrary.utils.imgload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.utils.imgload.imgprogress.ImgLoadCallBack;
import com.fy.baselibrary.utils.imgload.imgprogress.ProgressInterceptor;
import com.fy.baselibrary.utils.imgload.imgprogress.ProgressListener;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * 图片加载工具类(目前使用 Glide)
 * <p>
 * Created by fangs on 2017/5/5.
 */
public class ImgLoadUtils {

    private ImgLoadUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 加载指定URL的图片
     * @param url
     * @param imageView
     */
    public static void loadImage(String url, int errorId, ImageView imageView) {
        if (imageView == null) return;
        RequestOptions options = new RequestOptions()
                .fallback(errorId)
                .error(errorId)
                .placeholder(errorId)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);
    }

    /**
     * 加载圆形 图片
     * @param url
     * @param imageView
     */
    public static void loadCircularBead(String url, int errorId, ImageView imageView) {
        if (imageView == null) return;
        RequestOptions options = new RequestOptions()
                .fallback(errorId)
                .error(errorId)
                .placeholder(errorId)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param url
     * @param errorId
     * @param imageView
     */
    public static void loadRadiusImg(String url, int errorId, ImageView imageView) {
        if (imageView == null) return;
        //加载圆角图片 通过RequestOptions扩展功能
        RequestOptions requestOptions = new RequestOptions()
                .fallback(errorId)
                .error(errorId)
                .placeholder(errorId)
                .transforms(new RoundedCorners(15))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 预加载 （把指定URL地址的图片 的原始尺寸保存到缓存中）
     * @param url
     */
    public static void preloadImg(Context context, String url) {
        Glide.with(context)
                .load(url)
                .preload();
    }


    /**
     * 异步获取 glide 缓存在磁盘的图片 的 被观察者
     * @param context
     * @param url
     */
    @SuppressLint("CheckResult")
    public static File getImgCachePath(Context context, String url) throws ExecutionException, InterruptedException {
        FutureTarget<File> target = Glide.with(context)
                .asFile()
                .load(url)
                .submit();//必须要用在子线程当中

        return target.get();
    }

    /**
     * 加载网络图片 带进度回调监听
     */
    public static void loadImgProgress(String url, int errorId, ImageView imageView, ImgLoadCallBack<Drawable> callBack) {
        if (imageView == null) return;
        ProgressInterceptor.addListener(url, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                L.e("glide", progress + "%");
                if (null != callBack) {
                    RequestUtils.runUiThread(new RequestUtils.OnRunUiThreadListener() {
                        @Override
                        public void onRun() {
                            callBack.onProgress(progress);
                        }
                    });
                }
            }
        });

        RequestOptions options = new RequestOptions()
                .error(errorId)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(imageView.getContext())
                .asDrawable()
                .load(url)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ProgressInterceptor.removeListener(url);
                        if (null != callBack)
                            callBack.onLoadFailed(e, model, target, isFirstResource);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ProgressInterceptor.removeListener(url);
                        if (null != callBack)
                            callBack.onResourceReady(resource, model, target, dataSource, isFirstResource);
                        return false;
                    }
                })
                .into(imageView);
    }

}
