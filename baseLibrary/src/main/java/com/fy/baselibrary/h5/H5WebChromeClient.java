package com.fy.baselibrary.h5;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * describe：默认的 WebChromeClient
 * Created by fangs on 2020/1/9 0009 下午 14:31.
 */
public class H5WebChromeClient extends WebChromeClient {

    private View loadLayout;
    private ProgressBar webProgress;
    private int progress = 70;//设置进度达到 70% 显示webview

    public H5WebChromeClient(View loadLayout, int progress) {
        this.loadLayout = loadLayout;
        this.progress = progress;
    }

    public H5WebChromeClient(ProgressBar webProgress) {
        this.webProgress = webProgress;
    }

    public H5WebChromeClient(View loadLayout, ProgressBar webProgress, int progress) {
        this.loadLayout = loadLayout;
        this.webProgress = webProgress;
        this.progress = progress;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress >= progress) loadLayout.setVisibility(View.GONE);

        if (null != webProgress){
            webProgress.setProgress(newProgress);
            webProgress.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
        }
    }

}
