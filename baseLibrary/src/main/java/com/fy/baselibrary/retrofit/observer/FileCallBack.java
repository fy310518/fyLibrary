package com.fy.baselibrary.retrofit.observer;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.converter.file.FileResponseBodyConverter;
import com.fy.baselibrary.retrofit.load.type.UpLoadType;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.TransfmtUtils;
import com.fy.baselibrary.utils.cache.SpfAgent;

import java.io.File;

/**
 * 文件上传、下载 请求 观察者 【增强 RequestBaseObserver，带 进度】
 * Created by fangs on 2018/5/21.
 */
public abstract class FileCallBack extends RequestBaseObserver<Object> {
    final String filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();
    File tempFile;
    String fileDownUrl;

    public FileCallBack() {}

    public FileCallBack(IProgressDialog pDialog) {
        super(pDialog);
    }

    public FileCallBack(Object context) {
        super(context);
    }

    public FileCallBack(String url) {
        init(url);
    }

    public FileCallBack(String url, IProgressDialog pDialog) {
        super(pDialog);
        init(url);
    }

    public FileCallBack(String url, Object context) {
        super(context);
        init(url);
    }

    //下载文件 初始化参数
    private void init(@NonNull String url){
        this.fileDownUrl = url;
        tempFile = FileUtils.getTempFile(url, filePath);
    }

    @Override
    public void onNext(Object obj) {
        if (obj instanceof Double) {//说明是进度【一般请求不会发射 double类型数据，有问题后面再 自己定义一个对象吧】
            String percent = TransfmtUtils.doubleToKeepTwoDecimalPlaces((Double) obj);
            downProgress(percent);
        } else {
            super.onNext(obj);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (!TextUtils.isEmpty(fileDownUrl)) {
            dismissProgress();
            int FileDownStatus = SpfAgent.init("").getInt(tempFile.getName() + Constant.FileDownStatus);
            if (FileDownStatus == 4) {
                File targetFile = FileUtils.getFile(fileDownUrl, filePath);
                downProgress("100");
                downSuccess(targetFile);
            } else {
                SpfAgent.init("").saveInt(tempFile.getName() + Constant.FileDownStatus, 3).commit(false);
                onFail();
            }
        } else {
            super.onError(e);
        }
    }

    @Override
    protected void onSuccess(Object obj) {
        if (obj instanceof UpLoadType){
            upLoadSuccess((UpLoadType) obj);
        } else {
            downSuccess((File) obj);
            FileResponseBodyConverter.removeListener(fileDownUrl);
        }
    }

    /**
     * 上传完成 回调
     * @param data
     */
    protected void upLoadSuccess(UpLoadType data){}

    /**
     * 下载完成 回调
     * @param file
     */
    protected void downSuccess(File file){}

    /**
     * 上传、下载 需重写此方法，更新进度
     * @param percent 进度百分比 数
     */
    protected void downProgress(String percent){}
}
