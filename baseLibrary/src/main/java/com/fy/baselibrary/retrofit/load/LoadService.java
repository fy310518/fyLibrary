package com.fy.baselibrary.retrofit.load;

import android.util.ArrayMap;

import com.fy.baselibrary.retrofit.load.up.UpLoadFileType;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 通用的 上传文件 下载文件 api
 * Created by fangs on 2018/6/1.
 */
public interface LoadService {

    /**
     * h5调用本地 请求封装 之 GET请求
     */
    @GET
    Observable<Object> jsInAndroidGetRequest(@Url String apiUrl,
                                                   @HeaderMap ArrayMap<String, Object> heads,
                                                   @QueryMap ArrayMap<String, Object> params);

    /**
     * h5调用本地 请求封装 之 POST请求
     */
    @FormUrlEncoded
    @POST
    Observable<Object> jsInAndroidPostRequest(@Url String apiUrl,
                                              @HeaderMap ArrayMap<String, Object> heads,
                                              @FieldMap ArrayMap<String, Object> params);

    /**
     * 通用 图文上传 (支持多图片) （参数注解：@Body；参数类型：MultipartBody）
     * params.put("uploadFile", "fileName");
     * params.put("filePathList", files);
     * params.put("LoadOnSubscribe", new LoadOnSubscribe());
     *
     * 注意：其它 文本参数 value 必须是 字符串类型（如下 token 参数）
     * params.put("token", "123");
     */
    @UpLoadFileType
    @POST()
    Observable<Object> uploadFile(@Url String apiUrl,
                                  @HeaderMap ArrayMap<String, Object> heads,
                                  @Body ArrayMap<String, Object> params);

    /**
     * 多图片上传 方式二（@Multipart：方法注解；@Part：参数注解；参数类型；MultipartBody.Part）
     * @param apiUrl
     * @param txtParams  文本参数，可多个 （转换方式：MultipartBody.Part.createFormData("key", "参数");）
     * @param files  文件
     * @return
     */
    @Multipart
    @POST
    Observable<Object> uploadFile2(@Url String apiUrl,
                                   @Part MultipartBody.Part txtParams,
                                   @Part MultipartBody.Part... files);


    /**
     * 断点下载
     *
     * @param downParam 下载参数，传下载区间使用 "bytes=" + startPos + "-"
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("RANGE") String downParam, @Url String url);

}
