package com.fy.baselibrary.aop.statusbar;

import android.app.Activity;

import com.fy.baselibrary.aop.annotation.StatusBar;
import com.fy.baselibrary.statusbar.MdStatusBar;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class StatusBarAspect {

    @Pointcut("execution(@com.fy.baselibrary.aop.annotation.StatusBar * *(..))" + " && @annotation(param)")
    public void statusBar(StatusBar param) {}

    @Before("statusBar(param)")
    public void clickFilterHook(JoinPoint joinPoint, StatusBar param) throws Throwable {

        Activity activity = null;
        final Object object = joinPoint.getThis();
        if (null == object) return;

        if (object instanceof Activity) {
            activity = ((Activity)object);
        }
        if (null == activity) return;

        MdStatusBar.StatusBuilder builder = MdStatusBar.StatusBuilder.init()
                .setStatusColor(param.statusColor(), param.statusAlpha(), param.statusStrColor())
                .setNavColor(param.navColor(), param.navAlpha(), param.navStrColor())
                .setApplyNav(param.applyNav());

        switch (param.statusOrNavModel()){
            case 0:
                builder.setColorBar(activity);
                break;
            case 1:
                builder.setTransparentBar(activity);
                break;
            case 2:
                builder.setColorBarForDrawer(activity);
                break;
        }

    }
}
