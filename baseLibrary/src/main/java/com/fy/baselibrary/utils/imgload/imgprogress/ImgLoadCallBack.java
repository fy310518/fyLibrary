package com.fy.baselibrary.utils.imgload.imgprogress;


import com.bumptech.glide.request.RequestListener;

/**
 * describe：定义 图片加载回调 接口
 * Created by fangs on 2020/1/19 0019 下午 15:49.
 */
public interface ImgLoadCallBack extends RequestListener {

    /**
     * 进度回调
     * @param progress
     */
    void onProgress(int progress);

}
