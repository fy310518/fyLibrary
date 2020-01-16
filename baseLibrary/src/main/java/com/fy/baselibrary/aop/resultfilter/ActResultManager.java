package com.fy.baselibrary.aop.resultfilter;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

/**
 * aop方式解决 activity 或者 fragment startActivityForResult 启动新的activity 接收返回结果，重写onActivityResult()方法，造成代码凌乱问题
 * 带回调结果的跳转，管理类
 * Created by fangs on 2017/5/9.
 */
public class ActResultManager {

    private HashMap<String, HashMap<Integer, ResultCallBack>> mMap;
    private static ActResultManager instance;

    private ActResultManager() {}

    public static ActResultManager getInstance() {
        if (instance == null) {
            synchronized (ActResultManager.class) {
                if (instance == null) {
                    instance = new ActResultManager();
                }
            }
        }
        return instance;
    }

    /**
     * 根据intent跳转界面（带回调结果）
     * @param context 当前activity
     * @param intent  意图
     * @param requestCode 请求码【可以是 控件id（R.id.tvTime）】
     * @param callBack 回调监听接口（回调结果会通过callBack 的回调方法返回）
     */
    public void startActivityForResult(Activity context, Intent intent, int requestCode, ResultCallBack callBack) {
        if (context == null || intent == null) return;

        if (callBack != null) {
            if (mMap == null) mMap = new HashMap<>();
            HashMap<Integer, ResultCallBack> resultMap = mMap.get(context.getClass().getName());
            if (resultMap == null) {
                resultMap = new HashMap<>();
                mMap.put(context.getClass().getName(), resultMap);
            }
            resultMap.put(requestCode, callBack);
        }
        requestCode = requestCode & 0x0000ffff;//此操作 只是为了 请求码可以是 控件id（请求码 Can only use lower 16 bits for requestCode）
        context.startActivityForResult(intent, requestCode);
    }

    public void afterActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (mMap != null) {
            HashMap<Integer, ResultCallBack> resultMap = mMap.get(activity.getClass().getName());
            if (resultMap != null) {

                for (Integer key : resultMap.keySet()) {//此处做循环处理 对应请求码是控件id的情况
                    if (requestCode == (key & 0x0000ffff)){
                        ResultCallBack callBack = resultMap.get(key);

                        if (callBack != null) {
                            callBack.onActResult(key, resultCode, data);
                            resultMap.remove(key);
                            if (resultMap.size() == 0) {
                                mMap.remove(activity.getClass().getName());
                                if (mMap.size() == 0) {
                                    mMap = null;
                                }
                            }
                        }

                        break;
                    }
                }

            }
        }
    }


}
