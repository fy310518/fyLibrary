package com.fy.baselibrary.aop.resultfilter;

import android.app.Activity;
import android.content.Intent;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ActResultAspect {

    @After("execution(* android.app.Activity.onActivityResult(..))")
    public void onActivityResultMethod(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        int requestCode = (int) args[0];
        int resultCode = (int) args[1];
        Intent data = (Intent) args[2];

        Activity activity = (Activity) joinPoint.getTarget();
        ActResultManager.getInstance().afterActivityResult(activity, requestCode, resultCode, data);
    }

}
