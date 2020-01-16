package com.fy.baselibrary.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.base.mvp.BaseMVPActivity;
import com.fy.baselibrary.statuslayout.LoadSirUtils;
import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.statuslayout.OnStatusAdapter;
import com.fy.baselibrary.statuslayout.StatusLayoutManager;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.JumpUtils;
import com.fy.baselibrary.utils.media.PlayUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.ResUtils;

import butterknife.ButterKnife;
import io.reactivex.subjects.BehaviorSubject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * activity 生命周期回调 (api 14+)
 * 注意：使用本框架 activity 与 activity 之间传递数据 统一使用 Bundle
 * Created by fangs on 2017/5/18.
 */
public class BaseActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = "lifeCycle --> ";
    public static int actNum;
    int designWidth;
    OnStatusAdapter adapter;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public BaseActivityLifecycleCallbacks(int designWidth, OnStatusAdapter adapter) {
        this.designWidth = designWidth;
        this.adapter = adapter;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        BaseActivityLifecycleCallbacks.actNum++;
        L.e(TAG + activity.getClass().getSimpleName(), "Create()   " + activity.getTaskId());
//todo 正式发布时候 解开以下注释
//        if (OSUtils.getRomType() == OSUtils.EMUI && onCheck(activity)){//是华为手机则 执行
//            activity.finish();
//            return;
//        }

//        ScreenUtils.setCustomDensity(activity, designWidth);

        if (activity instanceof BaseMVPActivity) {
            ((BaseMVPActivity)activity).initPresenter();
        }

        BaseActivityBean activityBean = new BaseActivityBean();
        activityBean.setSubject(BehaviorSubject.create());

        IBaseActivity act = null;
        if (activity instanceof IBaseActivity) {
            act = (IBaseActivity) activity;
            if (act.setView() != 0) {
                if (act.isShowHeadView()) {//动态添加标题栏

                    View titleBar = initHead(activity);

                    View view = LayoutInflater.from(activity).inflate(act.setView(), null);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
                    LinearLayout linearLRoot = new LinearLayout(activity);
                    linearLRoot.setOrientation(LinearLayout.VERTICAL);
                    linearLRoot.setLayoutParams(params);
                    linearLRoot.addView(titleBar, MATCH_PARENT, WRAP_CONTENT);
                    linearLRoot.addView(view, MATCH_PARENT, MATCH_PARENT);

                    activity.setContentView(linearLRoot);
                } else {
                    activity.setContentView(act.setView());
                }
            }

            //设置 黄油刀 简化 Android 样板代码
            activityBean.setUnbinder(ButterKnife.bind(activity));


//        注册屏幕旋转监听
            if (Constant.isOrientation){
                BaseOrientoinListener orientoinListener = new BaseOrientoinListener(activity);
                boolean autoRotateOn = (android.provider.Settings.System.getInt(activity.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1);

                //检查系统是否开启自动旋转
                if (autoRotateOn) orientoinListener.enable();
                activityBean.setOrientoinListener(orientoinListener);
            }

            //设置 activity 多状态布局
            if (activity instanceof OnSetStatusView) {
                StatusLayoutManager slManager = LoadSirUtils.initStatusLayout(activity);
                activityBean.setSlManager(slManager);
            }
        }


        activity.getIntent().putExtra("ActivityBean", activityBean);
        //基础配置 执行完成，再执行 初始化 activity 操作
        if (null != act) act.initData(activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        L.e(TAG + activity.getClass().getSimpleName(), "--Start()");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        L.e(TAG + simpleName, "--Resume()");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        L.e(TAG + simpleName, "--Pause()");

        PlayUtils.getInstance().pause();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        L.e(TAG + activity.getClass().getSimpleName(), "--Stop()");

        PlayUtils.getInstance().stop();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        L.e(TAG + activity.getClass().getSimpleName(), "--SaveInstanceState()");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        L.e(TAG + activity.getClass().getSimpleName(), "--Destroy()");
        PlayUtils.getInstance().release();

        BaseActivityBean activityBean = (BaseActivityBean) activity.getIntent()
                .getSerializableExtra("ActivityBean");

        if (null != activityBean) {
            //解绑定 黄油刀
            if (null != activityBean.getUnbinder()) activityBean.getUnbinder().unbind();
            //销毁 屏幕旋转监听
            if (null != activityBean.getOrientoinListener())
                activityBean.getOrientoinListener().disable();

            if (null != activityBean.getSubject())
                activityBean.getSubject().onNext(Constant.DESTROY);
        }
    }

    /**
     * 初始化 toolbar
     *
     * @param activity
     */
    private View initHead(Activity activity) {
        View titleBar = LayoutInflater.from(activity).inflate(R.layout.activity_head, null);
        //这里全局给Activity设置toolbar和title mate
        Toolbar toolbar = titleBar.findViewById(R.id.toolbar);

        if (ConfigUtils.isTitleCenter()) {
            toolbar.setTitle("");
            TextView toolbarTitle = titleBar.findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(activity.getTitle());
            toolbarTitle.setTextColor(ResUtils.getColor(ConfigUtils.getTitleColor()));
            toolbarTitle.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(activity.getTitle());
        }

        if (activity instanceof AppCompatActivity) {
            AppCompatActivity act = (AppCompatActivity) activity;
            //设置导航图标要在setSupportActionBar方法之后
            act.setSupportActionBar(toolbar);
            //在Toolbar左边显示一个返回按钮
            act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //替换toolbar 自带的返回按钮
            if (ConfigUtils.getBackImg() > 0) toolbar.setNavigationIcon(ConfigUtils.getBackImg());
            //设置返回按钮监听事件
            toolbar.setNavigationOnClickListener(v -> JumpUtils.exitActivity(act));
            if (ConfigUtils.getBgColor() > 0)
                toolbar.setBackgroundColor(ResUtils.getColor(ConfigUtils.getBgColor()));
        }

        return titleBar;
    }

    /**
     * 判断 应用是否被杀死（拦截 华为手机 设置中关闭权限 应用崩溃重启 黑屏问题）
     * @param activity
     * @return
     */
    private boolean onCheck(Activity activity) {
        boolean isrun;
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);

        if (BaseActivityLifecycleCallbacks.actNum == 1 &&
                info.numRunning == 1 &&
                !info.topActivity.getClassName().equals("com.fy.baselibrary.startactivity.StartActivity")) {
            //被杀死重启
            isrun = true;
            L.e(TAG, activity.getClass().getName() + "关闭此界面");
        } else {
            isrun = false;//手动启动
        }

        return isrun;
    }

}
