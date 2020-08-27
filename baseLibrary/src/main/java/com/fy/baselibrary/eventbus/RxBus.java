package com.fy.baselibrary.eventbus;

import android.util.ArrayMap;

import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * 函数响应式编程 之 RxBus
 * Created by fangs on 2017/5/16.
 */
public class RxBus {
    private final FlowableProcessor<Object> mBus;
    private ArrayMap<String, Disposable> disposables;

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized();
        disposables = new ArrayMap<>();
    }

    private static class Holder {
        private static RxBus instance = new RxBus();
    }

    public static RxBus getInstance() {
        return Holder.instance;
    }

    public void send(@NonNull Object obj) {
        mBus.onNext(obj);
    }

    public <T> void register(String type, Class<T> clz, Consumer<T> onNext) {
        Disposable disposable = mBus.ofType(clz).subscribe(onNext);
        disposables.put(type, disposable);
    }

    public void unregisterAll() {
        disposables.clear();
        //会将所有由mBus 生成的 Flowable 都置  completed 状态  后续的 所有消息  都收不到了
        mBus.onComplete();
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    //取消订阅
    public void dispose(String type){
        Disposable disposable = disposables.get(type);
        if (null != disposable && !disposable.isDisposed()) disposable.dispose();
    }
}
