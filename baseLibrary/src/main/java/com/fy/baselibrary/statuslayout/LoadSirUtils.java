package com.fy.baselibrary.statuslayout;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.Constant;

/**
 * 多状态布局 替换逻辑工具类
 * Created by fangs on 2017/12/18.
 */
public class LoadSirUtils {

    /**
     * 替换逻辑
     * @param target
     * @return
     */
    public static TargetContext getTargetContext(View target) {
        Context context = target.getContext();
        ViewGroup contentParent = (ViewGroup) target.getParent();

        //此处判断只是为了不崩溃
        if (null == contentParent) {
            if (target instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) target;
                contentParent = vg.getParent() == null ? vg : (ViewGroup) vg.getParent();
            } else {
                throw new IllegalArgumentException("Must have a parent view");
            }
        }

        int childCount = contentParent == null ? 0 : contentParent.getChildCount();

        return new TargetContext(context, contentParent, target, childCount);
    }

    /**
     * 设置 多状态视图 管理器
     * @param contextObj
     */
    public static StatusLayoutManager initStatusLayout(Object contextObj){

        Context context;
        if (contextObj instanceof Activity) {
            context = (Activity) contextObj;
        } else if (contextObj instanceof Fragment) {
            context = ((Fragment) contextObj).getContext();
        } else {
            throw new IllegalArgumentException("The Context must be is Activity or Fragment.");
        }

        View target;
        OnSetStatusView listener;
        if (contextObj instanceof OnSetStatusView) {
            //获取 点击重试回调接口
            listener = (OnSetStatusView) contextObj;
            target = ((OnSetStatusView) contextObj).setStatusView();

        } else {
            throw new IllegalArgumentException("The param must be is 'StatusLayout.OnSetStatusView'.");
        }

        //构造 StatusLayoutManager 并显示内容布局，
        OnStatusAdapter statusAdapter = ConfigUtils.getOnStatusAdapter();
        return StatusLayoutManager.newBuilder(context, target)
                .errorView(null !=statusAdapter ? statusAdapter.errorViewId() : R.layout.state_include_error)
                .netWorkErrorView(null !=statusAdapter ? statusAdapter.netWorkErrorView() : R.layout.state_include_networkerror)
                .emptyDataView(null !=statusAdapter ? statusAdapter.emptyDataView() : R.layout.state_include_emptydata)
                .retryViewId(null !=statusAdapter ? statusAdapter.retryViewId() : R.id.tvTry)
                .onRetryListener(listener)
                .build()
                .showHideViewFlag(Constant.LAYOUT_CONTENT_ID);
    }
}
