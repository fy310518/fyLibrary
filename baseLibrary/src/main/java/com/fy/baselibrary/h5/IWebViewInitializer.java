package com.fy.baselibrary.h5;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * describe：定义接口 方便 H5WebFragment 获取 webView 以及 WebViewClient、WebChromeClient
 * Created by fangs on 2020/1/9 0009 上午 11:49.
 */
public interface IWebViewInitializer {

    /**
     * 获取 网页 加载 url
     * 方式1：加载一个网页
     * webView.loadUrl("http://www.google.com/");
     *
     * 方式2：加载apk包中 assets 目录下的html页面
     * webView.loadUrl("file:///android_asset/test.html");
     *
     * 方式3：加载手机SD卡 html页面
     * webView.loadUrl("file:///sdcard/test.html");
     */
    String getLoadUrl();

    //把 webView 传递到 H5WebFragment
    WebView getWebView();

    //针对浏览器本身行为的控制，如前进后退的回调
    WebViewClient initWebViewClient();

    //针对页面的控制,如js交互
    WebChromeClient initWebChromeClient();

    /**
     * 定义 JS 与 Android 交互类
     */
    BaseAndroidJSInterface getJsInterface();
    //定义 JS 与 Android 交互类名
    String getInterfaceName();
}
