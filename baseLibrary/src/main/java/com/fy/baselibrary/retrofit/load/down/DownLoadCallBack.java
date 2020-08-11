package com.fy.baselibrary.retrofit.load.down;


import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.retrofit.observer.IProgressDialog;
import com.fy.baselibrary.retrofit.observer.RequestBaseObserver;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.cache.SpfAgent;

import java.io.File;

/**
 * description 文件下载 观察者
 * Created by fangs on 2020/8/11 11:37.
 */
public abstract class DownLoadCallBack<T> extends RequestBaseObserver<T> {

    String url;
    String filePath;
    File tempFile;
    DownLoadListener<File> loadListener;

    public DownLoadCallBack() {}

    public DownLoadCallBack(IProgressDialog pDialog, String url, DownLoadListener<File> loadListener) {
        super(pDialog);
        this.loadListener = loadListener;
        init(url);
    }

    private void init(String url){
        this.url = url;
        filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();
        tempFile = FileUtils.getTempFile(url, filePath);
    }

    @Override
    public void onError(Throwable e) {
        int FileDownStatus = SpfAgent.init("").getInt(tempFile.getName() + Constant.FileDownStatus);
        if (FileDownStatus == 4) {
            File targetFile = FileUtils.getFile(url, filePath);
            loadListener.onProgress("100");
            RequestUtils.runUiThread(() -> {
                loadListener.onSuccess(targetFile);
            });
        } else {
//                            super.onError(e);
            SpfAgent.init("").saveInt(tempFile.getName() + Constant.FileDownStatus, 3).commit(false);
            RequestUtils.runUiThread(loadListener::onFail);
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();

        int fileDownStatus = SpfAgent.init("").getInt(tempFile.getName() + Constant.FileDownStatus);
        if (fileDownStatus != 4) {
            SpfAgent.init("")
                    .saveInt(tempFile.getName() + Constant.FileDownStatus, 3)
                    .commit(false);
        }
    }
}
