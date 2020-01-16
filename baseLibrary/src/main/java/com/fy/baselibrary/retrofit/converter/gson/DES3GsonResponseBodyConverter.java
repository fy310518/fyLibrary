package com.fy.baselibrary.retrofit.converter.gson;

import com.fy.baselibrary.utils.notify.L;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

/*
* 对联网返回数据的解密后统一处理。
* @author Alois
* create at 2017/4/25 上午 10:09
*/
public class DES3GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    DES3GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
//        String response = Des3.decode(value.string());
        String response = value.string();

        L.e("后台数据", response);
//        BaseResponseBean baseResponseBean = gson.fromJson(response, BaseResponseBean.class);
//        if(baseResponseBean.isTokenTimeOut()){
//            value.close();
//            throw new TokenTimeOutException();
//        }
//        else if (baseResponseBean.isCodeInvalid()) {
//            value.close();
////            throw new ApiException(baseResponseBean.getResultCode(), baseResponseBean.getResultMessage());
//        }

        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);

        try {
            return adapter.read(jsonReader);
        }
        finally {
            value.close();
        }
    }

}