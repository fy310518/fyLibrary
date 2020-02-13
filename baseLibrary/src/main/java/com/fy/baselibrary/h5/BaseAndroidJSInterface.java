package com.fy.baselibrary.h5;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.retrofit.RxHelper;
import com.fy.baselibrary.retrofit.load.LoadOnSubscribe;
import com.fy.baselibrary.retrofit.load.LoadService;
import com.fy.baselibrary.retrofit.observer.IProgressDialog;
import com.fy.baselibrary.retrofit.observer.RequestBaseObserver;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.GsonUtils;
import com.fy.baselibrary.utils.security.EncodeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * DESCRIPTION：h5 android 交互
 * Created by fangs on 2019/3/27 17:03.
 */
public class BaseAndroidJSInterface {

    protected Activity context;
    protected Fragment fragment;
    protected WebView view;
    private String host;

    private ArrayMap<String, String> defaultParams = new ArrayMap<>();
    private IProgressDialog progressDialog;

    public BaseAndroidJSInterface(Fragment fragment, WebView view, String host) {
        this.fragment = fragment;
        this.view = view;
        this.host = host;
    }

    public BaseAndroidJSInterface(Activity activity, WebView view, String host) {
        this.context = activity;
        this.view = view;
        this.host = host;
    }

    /**
     * 设置 加载弹窗
     *
     * @param progressDialog
     */
    public void setProgressDialog(IProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    /**
     * 设置 请求参数
     *
     * @param key
     * @param value
     * @return
     */
    public BaseAndroidJSInterface setDefaultParams(String key, String value) {
        defaultParams.put(key, value);
        return this;
    }

    /**
     * 获取 指定 key 的参数
     *
     * @param key
     * @param <T>
     */
    public <T> T getDefaultParams(String key) {
        return (T) defaultParams.get(key);
    }

    //解析 H5RequestBean 获取 请求参数
    private ArrayMap<String, Object> getHttpParams(H5RequestBean request) {
        ArrayMap<String, Object> oneParams = new ArrayMap<>();
        ArrayMap<String, Object> params = request.getParams();

        if (!defaultParams.isEmpty()) {
            for (String key : defaultParams.keySet()) {
                oneParams.put(key, defaultParams.get(key));
            }
        }

        if (!params.isEmpty()) {
            for (String key : params.keySet()) {
                //params.get(key) 不为空 并且 defaultParams map 中存在这个key 则 不添加
                if (TextUtils.isEmpty((CharSequence) params.get(key)) && defaultParams.containsKey(key)) {
                    continue;
                }

                oneParams.put(key, params.get(key));
            }
        }

        return oneParams;
    }

    //添加请求头
    private ArrayMap<String, Object> getHeaderParams(H5RequestBean request) {
        ArrayMap<String, Object> params = request.getHeader();
        if (null == params) {
            params = new ArrayMap<>();
//            params.put("Content-Type", "multipart/form-data;charse=UTF-8");
//            params.put("Connection", "keep-alive");
//            params.put("Accept", "*/*");
//            params.put("app-type", "Android");
        }

        return params;
    }

    /**
     * 定义本地网络请求方法 供 h5 调用
     *
     * @param requestContent h5 传递的 网络请求 请求头，请求方法（get，post），请求参数，请求 url
     * @return ""
     */
    @JavascriptInterface
    public String httpRequest(String requestContent) {
        return httpRequest("", requestContent);
    }

    /**
     * 定义本地网络请求方法 供 h5 调用
     *
     * @param hostIp         请求的主机地址 可为空，为空则表示 使用构造方法传递的 host
     * @param requestContent h5 传递的 网络请求 请求头，请求方法（get，post），请求参数，请求 url
     * @return
     */
    @JavascriptInterface
    public String httpRequest(String hostIp, String requestContent) {
        H5RequestBean request = null;
        try {
            request = GsonUtils.fromJson(requestContent, H5RequestBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "{错误提示}";//todo 返回 json格式 错误信息
        }

        if (TextUtils.isEmpty(request.getUrl())) {
            return "{错误提示}";//todo 返回 json格式 错误信息
        }

        String method = request.getRequestMethod();
        ArrayMap<String, Object> headers = getHeaderParams(request);
        ArrayMap<String, Object> params = getHttpParams(request);

        String hostAddress = !TextUtils.isEmpty(hostIp) ? hostIp : this.host;

        switch (method.toUpperCase()) {
            case "GET":
                httpGet(headers, params, hostAddress + request.getUrl(), request.getJsMethod());
                break;
            case "POST":
                httpPost(headers, params, hostAddress + request.getUrl(), request.getJsMethod());
                break;
            case "UPLOAD":
                httpUpload(headers, params, hostAddress + request.getUrl(), request.getJsMethod(), request.getBase64());
                break;
        }

        return "";
    }


    private void httpGet(ArrayMap<String, Object> headers, ArrayMap<String, Object> params, final String url, final String jsMethod) {
        RequestUtils.create(LoadService.class)
                .jsInAndroidGetRequest(url, headers, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxHelper.bindToLifecycle(null == context ? fragment.getActivity() : context))
                .subscribe(getCallObserver(url, jsMethod));
    }

    private void httpPost(ArrayMap<String, Object> headers, ArrayMap<String, Object> params, final String url, final String jsMethod) {
        RequestUtils.create(LoadService.class)
                .jsInAndroidPostRequest(url, headers, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxHelper.bindToLifecycle(null == context ? fragment.getActivity() : context))
                .subscribe(getCallObserver(url, jsMethod));
    }

    private void httpUpload(ArrayMap<String, Object> headers,
                            ArrayMap<String, Object> params,
                            final String url,
                            final String jsMethod,
                            ArrayList<String> base64) {

        Observable.just(base64)
                .subscribeOn(Schedulers.io())
                .map(new Function<ArrayList<String>, List<String>>() {
                    @Override
                    public List<String> apply(ArrayList<String> base64List) throws Exception {
                        List<String> filePath = new ArrayList<>();

                        for (String item : base64List) {
                            File newFile = FileUtils.createFile("/DCIM/camera/", "IMG_", ".png", 2);
                            EncodeUtils.decoderBase64File(item, newFile.getPath());

                            filePath.add(newFile.getPath());
                        }
                        return filePath;
                    }
                })
                .flatMap(new Function<List<String>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(List<String> filePathList) throws Exception {
                        params.put("uploadFile", "fileName");
                        params.put("filePathList", filePathList);
                        params.put("LoadOnSubscribe", new LoadOnSubscribe());

                        return RequestUtils.create(LoadService.class)
                                .uploadFile(url, headers, params)
                                .compose(RxHelper.bindToLifecycle(null == context ? fragment.getActivity() : context))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(getCallObserver(url, jsMethod));
    }

    //定义网络请求 观察者，统一处理返回数据
    private RequestBaseObserver<Object> getCallObserver(final String url, final String jsMethod) {
        return new RequestBaseObserver<Object>(progressDialog) {
            @Override
            protected void onSuccess(Object data) {
                //Android 调用 h5 方法
                String json = GsonUtils.toJson(data);
                view.loadUrl("javascript:" + jsMethod + "(\'" + json + "\')");

                if (listener != null) {
                    listener.beforH5(url, json, false);
                }
            }
        };
    }

    private OnOwnOptListener listener;

    public void setOnOwnOptListener(OnOwnOptListener listener) {
        this.listener = listener;
    }

    public interface OnOwnOptListener {
        String beforH5(String url, String data, boolean isError);
    }

    @JavascriptInterface
    public void back() {
        Activity act = null == context ? fragment.getActivity() : context;
        assert act != null;
        act.finish();
    }

    @JavascriptInterface
    public void webViewback() {
        if (this.view != null && this.view.canGoBack()) {
            this.view.goBack();
        } else {
            back();
        }
    }

}
