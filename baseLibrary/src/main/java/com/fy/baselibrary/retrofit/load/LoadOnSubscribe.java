package com.fy.baselibrary.retrofit.load;

import com.fy.baselibrary.utils.notify.L;

import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * 文件上传，下载 进度观察者 发射器（计算上传百分比）
 * Created by fangs on 2018/5/21.
 */
public class LoadOnSubscribe implements ObservableOnSubscribe<Double> {

    private ObservableEmitter<Double> mObservableEmitter;//进度观察者 发射器
    private long mSumLength = 0L;//总长度
    private AtomicLong uploaded = new AtomicLong();//已经上传 长度

    private double mPercent = 0;//已经上传进度 百分比

    public LoadOnSubscribe() {}

    @Override
    public void subscribe(ObservableEmitter<Double> e) throws Exception {
        this.mObservableEmitter = e;
    }

    public void setmSumLength(long mSumLength) {
        this.mSumLength = mSumLength;
    }

    public void onRead(long read) {
        uploaded.addAndGet(read);

        if (mSumLength <= 0) {
            onProgress(0);
        } else {
            onProgress(100d * uploaded.get() / mSumLength);
        }
    }

    private void onProgress(double percent) {
        if (null == mObservableEmitter || percent == mPercent) return;

        mPercent = percent;
        L.e("文件下载", percent + " %");
        mObservableEmitter.onNext(percent);

        if (percent >= 100.0) mObservableEmitter.onComplete();
    }

    //下载失败
    public void onError(@NonNull Throwable error){
        mObservableEmitter.onError(error);
    }

    //下载完成 清理进度数据
    public void clean() {
        this.mPercent = 0;
        this.uploaded = null;
        this.mSumLength = 0L;

        mObservableEmitter.onComplete();
    }
}
