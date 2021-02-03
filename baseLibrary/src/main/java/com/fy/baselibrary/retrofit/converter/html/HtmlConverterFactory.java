package com.fy.baselibrary.retrofit.converter.html;

import org.jsoup.nodes.Document;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 返回 HTML格式数据 转换器工厂
 * Created by fangs on 2018/11/12.
 */
public class HtmlConverterFactory extends Converter.Factory {

    private HtmlConverterFactory() {}

    public static HtmlConverterFactory create() {
        return new HtmlConverterFactory();
    }

    @Override
    public Converter<ResponseBody, Document> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof HtmlBodyType) {
                return new HtmlResponseBodyConverter();
            }
        }
        return null;
    }

}
