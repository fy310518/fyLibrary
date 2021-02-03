package com.fy.baselibrary.retrofit.converter.html;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * describe：定义一个方法注解 HtmlBodyType，用于请求返回数据是 HTML 格式， retrofit 匹配对应的 converter
 * Created by fangs on 2018/8/27 15:32.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HtmlBodyType {
}
