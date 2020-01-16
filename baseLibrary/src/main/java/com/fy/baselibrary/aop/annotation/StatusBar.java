package com.fy.baselibrary.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义一个 方法注解 StatusBar，
 * 设置当前 activity 状态栏导航栏颜色和透明度
 * Created by fangs on 2018/8/24 14:00.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StatusBar {

    /**
     * 状态栏颜色
     * @return
     */
    int statusColor() default 0;

    /** 资源名称（加此参数是为了在 module中 不能使用colors资源时候的折中办法） */
    String statusStrColor() default "";

    /**
     * 状态栏透明度
     * @return
     */
    int statusAlpha() default 0;


    /**
     * 导航栏颜色
     * @return
     */
    int navColor() default 0;

    /** 资源名称（加此参数是为了在 module中 不能使用colors资源时候的折中办法） */
    String navStrColor() default "";

    /**
     * 导航栏 透明度
     * @return
     */
    int navAlpha() default 0;


    /**
     * 设置状态栏、导航栏 模式 值为0、1、2
     * @return
     */
    int statusOrNavModel() default 0;

    /**
     * 导航栏【是否】悬浮在界面之上
     * @return
     */
    boolean applyNav() default true;

}
