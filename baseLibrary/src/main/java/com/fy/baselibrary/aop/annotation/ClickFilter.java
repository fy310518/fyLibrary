package com.fy.baselibrary.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法注解 ClickFilter，处理按钮重复点击
 * Created by fangs on 2018/8/24 14:00.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ClickFilter {

    /** 点击时间间隔 */
    long value() default 900;

}
