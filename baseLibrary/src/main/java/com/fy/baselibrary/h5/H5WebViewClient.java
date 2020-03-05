package com.fy.baselibrary.h5;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.statuslayout.StatusLayoutManager;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.net.NetUtils;

/**
 * describe：默认的 WebViewClient
 * Created by fangs on 2020/1/9 0009 下午 14:28.
 */
public class H5WebViewClient extends WebViewClient {

    private OnSetStatusView onSetStatusView;

    public H5WebViewClient(OnSetStatusView onSetStatusView) {
        this.onSetStatusView = onSetStatusView;
    }

    //在开始加载网页时会回调
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        view.getSettings().setBlockNetworkImage(true);
    }

    //加载完成的时候会回调
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.getSettings().setBlockNetworkImage(false);
        if (!view.getSettings().getLoadsImagesAutomatically()) {
            //设置wenView加载图片资源
            view.getSettings().setBlockNetworkImage(false);
            view.getSettings().setLoadsImagesAutomatically(true);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    //当加载的网页需要重定向的时候就会回调这个函数告知我们应用程序是否需要接管控制网页加载，如果应用程序接管，
    //并且return true意味着主程序接管网页加载，如果返回false让webview自己处理。
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if (Uri.parse(url).getHost().equals("www.baidu.com")) {
//            return true;
//        }
//        view.loadUrl(url);
//        return false;

        WebView.HitTestResult hitTestResult = view.getHitTestResult();
        //hitTestResult==null 解决重定向问题(刷新后不能退出的bug)
        if (!TextUtils.isEmpty(url) && hitTestResult == null) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    //加载错误的时候会回调
     @Override
     public void onReceivedError(WebView webView, int i, String s, String s1) {
         super.onReceivedError(webView, i, s, s1);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return;

         if (null != onSetStatusView) {
             onSetStatusView.showHideViewFlag(!NetUtils.isConnected() ? Constant.LAYOUT_NETWORK_ERROR_ID : Constant.LAYOUT_ERROR_ID);
         }
     }

    //加载错误的时候会回调
     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     @Override
     public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        super.onReceivedError(webView, webResourceRequest, webResourceError);
        if (webResourceRequest.isForMainFrame()) {
            if (null != onSetStatusView) onSetStatusView.showHideViewFlag(!NetUtils.isConnected() ? Constant.LAYOUT_NETWORK_ERROR_ID : Constant.LAYOUT_ERROR_ID);
        }
    }


}
