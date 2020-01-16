package com.fy.baselibrary.retrofit.converter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Alois on 2017/5/22.
 */
public class DES3GsonConverterFactory extends Converter.Factory{

    public static DES3GsonConverterFactory create() {
        return create(new Gson());
    }

    public static DES3GsonConverterFactory create(Gson gson) {
        return new DES3GsonConverterFactory(gson);
    }

    private final Gson gson;

    private DES3GsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new DES3GsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new DES3GsonRequestBodyConverter<>(gson, adapter);
    }
}
