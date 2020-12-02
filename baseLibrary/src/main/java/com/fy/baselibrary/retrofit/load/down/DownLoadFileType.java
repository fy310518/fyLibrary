package com.fy.baselibrary.retrofit.load.down;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * describe：定义一个方法注解 DownLoadFileType，用于下载文件时候 retrofit 匹配对应的 converter
 * Created by fangs on 2018/8/27 15:32.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DownLoadFileType {
}
