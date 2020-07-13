package com.fy.baselibrary.retrofit;

import android.os.Handler;
import android.os.Looper;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.converter.file.FileResponseBodyConverter;
import com.fy.baselibrary.retrofit.observer.CallBack;
import com.fy.baselibrary.retrofit.load.LoadOnSubscribe;
import com.fy.baselibrary.retrofit.load.LoadService;
import com.fy.baselibrary.retrofit.load.down.DownLoadListener;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.cache.ACache;
import com.fy.baselibrary.utils.cache.SpfAgent;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.Serializable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * 网络请求入口
 * Created by fangs on 2018/3/13.
 */
public class RequestUtils {

    public volatile static RequestUtils instance;

    @Inject
    protected Retrofit netRetrofit;

    @Inject
    protected OkHttpClient.Builder okBuilder;//使 上层依赖 可以获得唯一的 OkHttpClient；

    protected CompositeDisposable mCompositeDisposable;

    private RequestUtils() {
        DaggerRequestComponent.builder().build().inJect(this);

        mCompositeDisposable = new CompositeDisposable();
    }

    public static synchronized RequestUtils getInstance() {
        if (null == instance) {
            synchronized (RequestUtils.class) {
                if (null == instance) {
                    instance = new RequestUtils();
                }
            }
        }

        return instance;
    }

    public static OkHttpClient.Builder getOkBuilder() {
        return getInstance().okBuilder;
    }

    /**
     * 得到 RxJava + Retrofit 被观察者 实体类
     *
     * @param clazz 被观察者 类（ApiService.class）
     * @param <T>   被观察者 实体类（ApiService）
     * @return 封装的网络请求api
     */
    public static <T> T create(Class<T> clazz) {
        return getInstance().netRetrofit.create(clazz);
    }

    /**
     * 同时从缓存和网络获取请求结果
     *
     * @param fromNetwork 从网络获取数据的 Observable
     * @param apiKey      key
     * @param <T>         泛型
     * @return 被观察者
     */
    public static <T> Observable<T> request(Observable<T> fromNetwork, String apiKey) {
        /** 定义读取缓存数据的 被观察者 */
        Observable<T> fromCache = Observable.create(new ObservableOnSubscribe<T>() {
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                ACache mCache = ACache.get(ConfigUtils.getAppCtx());
                T cache = (T) mCache.getAsObject(apiKey);
                if (null != cache) {
                    L.e("net cache", cache.toString());
                    emitter.onNext(cache);
                }
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        /**
         * 使用doOnNext 操作符，对网络请求的数据 缓存到本地
         * 这里的fromNetwork 不需要指定Schedule,在handleRequest中已经变换了
         */
        fromNetwork = fromNetwork.doOnNext(new Consumer<T>() {
            @Override
            public void accept(T result) throws Exception {
                L.e("net doOnNext", result.toString());

                ACache mCache = ACache.get(ConfigUtils.getAppCtx());
                if (result instanceof Serializable){
                    mCache.put(apiKey, (Serializable) result);
                } else {
                    mCache.put(apiKey, result.toString());
                }
            }
        });

        return Observable.concat(fromCache, fromNetwork);
    }

    /**
     * 文件下载
     * @param url
     * @param loadListener
     */
    public static void downLoadFile(String url, DownLoadListener<File> loadListener){
        final String filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();
        final File tempFile = FileUtils.getTempFile(url, filePath);

        LoadOnSubscribe loadOnSubscribe = new LoadOnSubscribe();

        Observable<File> downloadObservable = Observable.just(url)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String downUrl) throws Exception {

                        File targetFile = FileUtils.getFile(downUrl, filePath);
                        if (targetFile.exists()) {
                            SpfAgent.init("").saveInt(tempFile.getName() + Constant.FileDownStatus, 4).commit(false);//下载完成
                            return "文件已下载";
                        } else {
                            return "bytes=" + tempFile.length() + "-";
                        }
                    }
                })
                .flatMap(new Function<String, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(String downParam) throws Exception {
                        L.e("fy_file_FileDownInterceptor", "文件下载开始---" + Thread.currentThread().getName());
                        if (downParam.startsWith("bytes=")) {
                            return RequestUtils.create(LoadService.class).download(downParam, url);
                        } else {
                            SpfAgent.init("").saveInt(tempFile.getName() + Constant.FileDownStatus, 4).commit(false);
                            return null;
                        }
                    }
                })
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        try {
                            //使用反射获得我们自定义的response
//                            Class aClass = responseBody.getClass();
//                            Field field = aClass.getDeclaredField("delegate");
//                            field.setAccessible(true);
//                            ResponseBody body = (ResponseBody) field.get(responseBody);
//                            if (body instanceof FileResponseBody) {
//                                FileResponseBody prBody = ((FileResponseBody) body);
                                L.e("fy_file_FileDownInterceptor", "文件下载 响应返回---" + Thread.currentThread().getName());
//                                return FileResponseBodyConverter.saveFile(loadOnSubscribe, prBody, prBody.getDownUrl(), filePath);
                                return FileResponseBodyConverter.saveFile(loadOnSubscribe, responseBody, url, filePath);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                });


        Observable.merge(Observable.create(loadOnSubscribe), downloadObservable)
                .subscribeOn(Schedulers.io())
                .subscribe(new CallBack<Object>() {
                    @Override
                    protected void onProgress(String percent) {
                        loadListener.onProgress(percent);
                    }

                    @Override
                    protected void onSuccess(Object file) {
                        if (file instanceof File) {
                            loadListener.onProgress("100");

                            runUiThread(() -> {
                                loadListener.onSuccess((File) file);//已在主线程中，可以更新UI
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        int FileDownStatus = SpfAgent.init("").getInt(tempFile.getName() + Constant.FileDownStatus);
                        if (FileDownStatus == 4) {
                            File targetFile = FileUtils.getFile(url, filePath);
                            loadListener.onProgress("100");
                            runUiThread(() -> {
                                loadListener.onSuccess(targetFile);
                            });
                        } else {
//                            super.onError(e);
                            SpfAgent.init("").saveInt(tempFile.getName() + Constant.FileDownStatus, 3).commit(false);
                            runUiThread(loadListener::onFail);
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();

                        int fileDownStatus = SpfAgent.init("").getInt(tempFile.getName() + Constant.FileDownStatus);
                        if (fileDownStatus != 4){
                            SpfAgent.init("")
                                    .saveInt(tempFile.getName() + Constant.FileDownStatus, 3)
                                    .commit(false);
                        }
                    }
                });
    }

    public interface OnRunUiThreadListener{
        void onRun();
    }

    /**
     * 定义 回调 UI线程
     * @param runUiThreadListener
     */
    public static void runUiThread(OnRunUiThreadListener runUiThreadListener){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            runUiThreadListener.onRun();
        });
    }

    /**
     * 使用RxJava CompositeDisposable 控制请求队列
     *
     * @param d 切断订阅事件 接口
     */
    public static void addDispose(Disposable d) {
        getInstance().mCompositeDisposable.add(d);
    }

    /**
     * 使用RxJava CompositeDisposable 清理所有的网络请求
     */
    public static void clearDispose() {
        getInstance().mCompositeDisposable.clear();
    }

}
