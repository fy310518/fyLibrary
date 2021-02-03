package com.fy.baselibrary.retrofit.converter.html;

import java.io.IOException;

import okhttp3.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import retrofit2.Converter;

/**
 * describe: 返回 HTML格式数据 转换器
 * Created by fangs on 2019/8/28 22:03.
 */
public class HtmlResponseBodyConverter implements Converter<ResponseBody, Document> {

    public HtmlResponseBodyConverter() {
    }

    @Override
    public Document convert(ResponseBody value) throws IOException {
        try {
            return Jsoup.parse(value.byteStream(), "UTF-8", "---");
        } finally {
            value.close();
        }
    }
}
