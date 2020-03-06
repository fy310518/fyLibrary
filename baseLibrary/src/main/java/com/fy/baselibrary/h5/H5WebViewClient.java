package com.fy.baselibrary.h5;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.utils.Constant;

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
        view.loadUrl(url);
        return false;
    }

    //加载错误的时候会回调
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
//        view.loadUrl("about:blank"); // 避免出现默认的错误界面
        // 断网或者网络连接超时
        if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
            setTips(Constant.LAYOUT_NETWORK_ERROR_ID);
        } else {
            setTips(Constant.LAYOUT_ERROR_ID);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)// 这个方法在6.0才出现
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);

//        view.loadUrl("about:blank");// 避免出现默认的错误界面
        int statusCode = errorResponse.getStatusCode();
        if (404 == statusCode || 500 == statusCode) {
            setTips(Constant.LAYOUT_ERROR_ID);
        } else {
            setTips(Constant.LAYOUT_NETWORK_ERROR_ID);
        }
    }

    private void setTips(int status){
        if (null != onSetStatusView) {
            onSetStatusView.showHideViewFlag(status);
        }
    }
}
