package com.fy.baselibrary.retrofit.converter.file;

import android.support.annotation.Nullable;

import com.fy.baselibrary.retrofit.load.up.UpLoadFileType;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 上传文件 转换器工厂
 * Created by fangs on 2018/11/12.
 */
public class FileConverterFactory extends Converter.Factory {

    private FileConverterFactory() {}

    public static FileConverterFactory create(){
        return new FileConverterFactory();
    }


    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        //进行条件判断，如果传进来的 methodAnnotations 不包含 UpLoadFileType，则匹配失败
        for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof UpLoadFileType) {
                return new FileRequestBodyConverter();
            }
        }

        return null;
    }

    @Override
    public Converter<ResponseBody, File> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return null;
    }
}
