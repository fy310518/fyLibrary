package com.fy.baselibrary.h5;

import android.webkit.WebView;

/**
 * describe：定义接口 方便 H5WebFragment 获取 webView 以及 WebViewClient、WebChromeClient
 * Created by fangs on 2020/1/9 0009 上午 11:49.
 */
public interface IWebViewInitializer {

    /**
     * 获取 网页 加载 url
     */
    String getLoadUrl();

    //把 webView 传递到 H5WebFragment
    WebView getWebView();

    //针对浏览器本身行为的控制，如前进后退的回调
    H5WebViewClient initWebViewClient();

    //针对页面的控制,如js交互
    H5WebChromeClient initWebChromeClient();

    /**
     * 定义 JS 与 Android 交互类
     */
    BaseAndroidJSInterface getJsInterface();
    //定义 JS 与 Android 交互类名
    String getInterfaceName();
}
