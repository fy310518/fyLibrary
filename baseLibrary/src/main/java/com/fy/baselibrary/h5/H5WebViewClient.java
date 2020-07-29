package com.fy.baselibrary.h5;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.imgload.ImgLoadUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * describe：默认的 WebViewClient
 * Created by fangs on 2020/1/9 0009 下午 14:28.
 */
public abstract class H5WebViewClient extends WebViewClient {

    public static String blank = "about:blank";
    private boolean mIsRedirect;
    private OnSetStatusView onSetStatusView;

    public H5WebViewClient(OnSetStatusView onSetStatusView) {
        this.onSetStatusView = onSetStatusView;
    }

    //在开始加载网页时会回调
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mIsRedirect = false;
        view.getSettings().setBlockNetworkImage(true);
        setTips(Constant.LAYOUT_CONTENT_ID);
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
        WebView.HitTestResult hitTestResult = view.getHitTestResult();
        //hitTestResult==null解决重定向问题(刷新后不能退出的bug)
        if (!TextUtils.isEmpty(url) && hitTestResult == null) {
            return true;
        }

        mIsRedirect = true;
        view.loadUrl(url);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override//webView 请求 拦截方法【下同】
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Nullable
    @Override//此 API 21后 过时
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        String mimeType = null;
        try {
            mimeType = new URL(url).openConnection().getContentType();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebResourceResponse webResourceResponse;
        if (isImgUrl(url)){//1、如果是图片
            webResourceResponse = getImgWebResResponse(url);
            if (null == webResourceResponse) webResourceResponse = super.shouldInterceptRequest(view, url);
            return webResourceResponse;
        } else if (isJsOrCssUrl(url)){
            webResourceResponse = getFileWebResResponse(url);
            if (null == webResourceResponse) webResourceResponse = super.shouldInterceptRequest(view, url);
            return webResourceResponse;
        } else if (isHtmlUrl(url, mimeType)){//html
            webResourceResponse = getFileWebResResponse(url);
            if (null == webResourceResponse) webResourceResponse = super.shouldInterceptRequest(view, url);
            return webResourceResponse;
        } else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    //加载错误的时候会回调
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);

//        view.loadUrl(blank); // 避免出现默认的错误界面
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

//        view.loadUrl(blank);// 避免出现默认的错误界面
        int statusCode = errorResponse.getStatusCode();

        if (400 == statusCode && request.getUrl().toString().toLowerCase().endsWith("favicon.ico")) return;//说明网页没有配置 网页 图标

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




    //判断是否是 图片
    private boolean isImgUrl(String url){
        if (TextUtils.isEmpty(url)) return false;

        url = url.toLowerCase();
        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif")  || url.endsWith(".svg")) return true;

        return false;
    }

    //判断是否是 js css文件
    private boolean isJsOrCssUrl(String url){
        if (TextUtils.isEmpty(url)) return false;

        url = url.toLowerCase();
        if (url.endsWith("js") || url.endsWith(".css") || url.endsWith(".woff") || url.contains(".js?") || url.contains(".css?") || url.contains(".woff?")) return true;

        return false;
    }

    //判断是否是 html 文件
    private boolean isHtmlUrl(String url, String mimeType){
        if (TextUtils.isEmpty(url)) return false;
        if (!TextUtils.isEmpty(mimeType) && mimeType.toLowerCase().contains("text/html"))

        url = url.toLowerCase();
        if (url.endsWith(".html") || url.endsWith(".htm") || url.contains(".html?") || url.contains(".htm?")) return true;

        return false;
    }

//    new WebResourceResponse("image/png","UTF-8",new FileInputStream(imgFile)) 第一个参数对应的如下：
//    js:mimeType ="application/x-javascript";
//    css:mimeType ="text/css";
//    html:mimeType ="text/html";
//    jpg/png: mimeType = "image/png";
//    woff: application/octet-stream
    private WebResourceResponse getImgWebResResponse(String url){
        WebResourceResponse webResourceResponse = null;
        File imgFile = null;
        try {
            imgFile = ImgLoadUtils.getImgCachePath(ConfigUtils.getAppCtx(), url);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (null != imgFile) {
                try {
                    L.e("H5 图片地址", imgFile.getPath() + "------");
                    webResourceResponse = new WebResourceResponse("image/png", "UTF-8", new FileInputStream(imgFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return webResourceResponse;
    }

    private WebResourceResponse getFileWebResResponse(String url){
        WebResourceResponse webResourceResponse = null;

        final String filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();

        File targetFile = FileUtils.getFile(url, filePath);
        if (targetFile.exists()) {
            try {
                String mimeType = new URL(url).openConnection().getContentType();
                L.e("H5 文件地址", targetFile.getPath() + "------");
                webResourceResponse = new WebResourceResponse(mimeType, "UTF-8", new FileInputStream(targetFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cacheH5Res(url);
        }

        return webResourceResponse;
    }

    /**
     * 缓存 需要下载的 网页资源【如：js文件，css文件 】
     */
    protected abstract void cacheH5Res(String url);

}
