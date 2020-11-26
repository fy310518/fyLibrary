package com.fy.baselibrary.retrofit.load;

import android.util.ArrayMap;

import com.fy.baselibrary.retrofit.load.up.UpLoadFileType;

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

//    应用层 新建 一个 xxService 接口，复杂下面 三个接口方法，beanModle 修改成 自己的
//    //普通get 请求
//    @GET
//    Observable<BeanModule<Object>> getCompose(@Url String apiUrl, @QueryMap ArrayMap<String, Object> params);
//
//    //普通post 请求【请求参数 json格式 】
//    @POST
//    Observable<BeanModule<Object>> postCompose(@Url String apiUrl, @Body ArrayMap<String, Object> params);
//
//    //普通post 请求【表单提交】
//    @FormUrlEncoded
//    @POST
//    Observable<BeanModule<Object>> postFormCompose(@Url String apiUrl, @FieldMap ArrayMap<String, Object> params);



    /**
     * h5调用本地 请求封装 之 GET请求
     */
    @GET
    Observable<Object> jsInAndroidGetRequest(@Url String apiUrl,
                                             @HeaderMap ArrayMap<String, Object> heads,
                                             @QueryMap ArrayMap<String, Object> params);

    /**
     * h5调用本地 请求封装 之 POST请求【表单】
     */
    @FormUrlEncoded
    @POST
    Observable<Object> jsInAndroidPostForm(@Url String apiUrl,
                                           @HeaderMap ArrayMap<String, Object> heads,
                                           @FieldMap ArrayMap<String, Object> params);

    /**
     * h5调用本地 请求封装 之 POST请求【json】
     */
    @POST
    Observable<Object> jsInAndroidPostJson(@Url String apiUrl,
                                           @HeaderMap ArrayMap<String, Object> heads,
                                           @Body ArrayMap<String, Object> params);

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
     * @param downParam 下载参数，传下载区间使用 "bytes=" + startPos + "-"
     *                  【IF-RANGE 如果服务器不支持分段下载，则直接下载整个文件】
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("IF-RANGE") String downParam, @Url String url);

}
