package com.fy.baselibrary.aop.clickfilter;

import android.view.View;

import com.fy.baselibrary.aop.annotation.ClickFilter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 对添加 @ClickFilter 注解的方法做统一的切面处理
 * Created by fangs on 2018/8/23 17:54.
 */
@Aspect
public class OnClikFilterAspect {

    @Pointcut("execution(@com.fy.baselibrary.aop.annotation.ClickFilter * *(..))")
    public void clickFilter() {}

    @Around("clickFilter()")
    public void clickFilterHook(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出方法的参数
        View view = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof View) {
                view = (View) arg;
                break;
            }
        }
        if (null == view) return;

        // 取出方法的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (!method.isAnnotationPresent(ClickFilter.class)) return;

        ClickFilter singleClick = method.getAnnotation(ClickFilter.class);
        // 判断是否快速点击
        if (!ClickUtils.isFastClick(view, singleClick.value())) {
            // 不是快速点击，执行原方法
            joinPoint.proceed();
        }
    }

}
