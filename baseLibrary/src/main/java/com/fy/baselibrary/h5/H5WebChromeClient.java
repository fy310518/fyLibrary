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

    public H5WebChromeClient(View loadLayout) {
        this.loadLayout = loadLayout;
    }

    public H5WebChromeClient(ProgressBar webProgress) {
        this.webProgress = webProgress;
    }

    public H5WebChromeClient(View loadLayout, ProgressBar webProgress) {
        this.loadLayout = loadLayout;
        this.webProgress = webProgress;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (null != loadLayout) loadLayout.setVisibility(newProgress > 80 ? View.GONE : View.VISIBLE);

        if (null != webProgress){
            webProgress.setProgress(newProgress);
            webProgress.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
        }
    }

}
