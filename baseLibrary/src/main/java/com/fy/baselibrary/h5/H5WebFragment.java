package com.fy.baselibrary.h5;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fy.baselibrary.base.fragment.BaseFragment;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.net.NetUtils;

/**
 * describe：简单封装 常用 webView 配置
 * Created by fangs on 2020/1/9 0009 上午 11:24.
 */
public abstract class H5WebFragment extends BaseFragment {

    WebView webView;
    IWebViewInitializer initializer;

    public abstract IWebViewInitializer setInitializer();

    @Override
    protected void baseInit() {
        Bundle bundle = getArguments();
        if (null != bundle) bundle.remove("ActivityBean");

        initWebView();
        initWebViewSetting();
        webViewSetting();

//        webView.loadUrl("file:///storage/emulated/0/index.html");//加载 SD卡下的 html【file:///后面跟html文件完整路径】
//        webView.loadUrl("file:///android_asset/index.html");//加载 assets 目录下的 html【file:///android_asset后面跟html文件完整路径】
//        webView.loadUrl("https://www.hao123.com/");//直接加载一个网页
        webView.loadUrl(initializer.getLoadUrl());
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView(){
        //获取子类回调传回来的接口实例
        initializer = setInitializer();
        if (null != initializer) {
            webView = initializer.getWebView();
            if (null == webView) {
                onBackPressed();
            } else {
                //第一个参数把自身传给js 第二个参数是this的一个名字
                webView.addJavascriptInterface(initializer.getJsInterface(), initializer.getInterfaceName());

                webView.setWebViewClient(initializer.initWebViewClient());
                webView.setWebChromeClient(initializer.initWebChromeClient());
            }
        } else {
            onBackPressed();
        }
    }

    //todo 注意 webView 不加 软硬件加速 待确认
    private void initWebViewSetting() {
        if (null == webView) return;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);  //设置与Js交互的权限
        settings.setDomStorageEnabled(true);//开启本地DOM存储
        settings.setAppCachePath(FileUtils.getCacheDir());
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheMaxSize(50 * 1024 * 1024);
        //设置 webView 缓存 模式
        settings.setCacheMode(NetUtils.isConnected() ? WebSettings.LOAD_DEFAULT : WebSettings.LOAD_CACHE_ELSE_NETWORK);

        settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        settings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.setAllowFileAccessFromFileURLs(true);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setSavePassword(false);// 关闭密码保存提醒功能
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式

        webView.setHorizontalScrollBarEnabled(false);//滚动条水平不显示
        webView.setVerticalScrollBarEnabled(false); //滚动条垂直不显示
//        webView.setVerticalScrollbarOverlay(true);/* 设置垂直滚动条是否有叠加样式 */
        settings.setSupportZoom(true);//缩放支持缩放
//        webView.setInitialScale(100);//设置缩放等级
        settings.setBuiltInZoomControls(true); /* 设置是否允许webView使用缩放的功能 */
        settings.setDisplayZoomControls(false);//隐藏webView缩放按钮 【api 11 以上】

        settings.setUseWideViewPort(true);/* 设置为使用webView推荐的窗口，主要是为了配合下一个属性 */
        settings.setLoadWithOverviewMode(true);/* 设置网页自适应屏幕大小，该属性必须和上一属性配合使用 */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    //可选操作，修改父类的 webView 设置
    protected void webViewSetting(){

    }


    @Override
    public View setStatusView(){return webView;}

    @Override
    public void onRetry() {
        webView.reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != webView){
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != webView){
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (null != webView) {
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            webView.getSettings().setJavaScriptEnabled(false);
//            webView.clearCache(true);//清理缓存；同时删除 setAppCachePath 设置的缓存文件夹里面的内容

            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

            webView.removeAllViews();
            //释放资源
            webView.destroy();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView = null;
        }

        System.gc();
        super.onDestroy();
    }


    /**
     * web页面 回退
     */
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            getActivity().finish();
        }
    }
}
