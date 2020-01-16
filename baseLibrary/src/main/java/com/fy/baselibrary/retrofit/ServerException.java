package com.fy.baselibrary.retrofit;

/**
 * 自定义 网络请求 异常
 * Created by fangs on 2017/12/12.
 */
public class ServerException extends RuntimeException {

    public int code;

    public ServerException(String msg, int code){
        super(msg);
        this.code = code;
    }


}
