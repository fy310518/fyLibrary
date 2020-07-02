package com.fy.baselibrary.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gson 工具类
 * Created by fangs on 2017/7/18.
 */
public class GsonUtils {

    private GsonUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 将bean转换成Json字符串
     * @param bean
     */
    public static String toJson(Object bean) {
        return new Gson().toJson(bean);
    }

    /**
     * 将 list 转换成json字符串
     * @return
     */
    public static <T> String listToJson(List<T> data){
        Gson gson = new Gson();

        return gson.toJson(data);
    }

    /**
     * 将 map 转换成 json字符串
     * @param params
     * @return
     */
    public static <T> String mapToJsonStr(Map<String, T> params){
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        return gson.toJson(params);
    }

    /**
     * 没有被 @Expose 标注的字段会被排除
     * @param bean
     */
    public static String toJsonExclude(Object bean) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        return gson.toJson(bean);
    }



    /**
     * 将Json字符串转换成对象
     * @param json
     * @param type
     */
    public static<T> T fromJson(String json, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    /**
     * 没有被 @Expose 标注的字段会被排除
     * @param json
     * @param type
     */
    public static<T> T fromJsonExclude(String json, Class<T> type) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.fromJson(json, type);
    }

    /**
     * 将Json字符串转换成对象
     * @param json
     * @param typeOfT  Type type = new TypeToken<BeanModule<CaseInfoBean>>(){}.getType();
     */
    public static<T> T fromJson(String json, Type typeOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, typeOfT);
    }

    /**
     * json字符串 转换成 json对象
     * @param jsonStr
     * @return
     */
    public static JsonObject jsonStrToJsonObj(String jsonStr){
        JsonObject returnData = new JsonParser().parse(jsonStr).getAsJsonObject();

        return returnData;
    }

    /**
     * 将Json字符串转换成 List集合
     * @param jsonStr
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> clazz) {
        List<T> lst = new ArrayList<>();

        JsonArray array = new JsonParser().parse(jsonStr).getAsJsonArray();
        for (final JsonElement elem : array) {
            lst.add(new Gson().fromJson(elem, clazz));
        }

        return lst;
    }

}
