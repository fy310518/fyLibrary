package com.fy.baselibrary.application.ioc;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;


/**
 * 应用框架基础配置工具类 （应用启动时候初始化）
 * Created by fangs on 2018/7/13.
 */
public class ConfigUtils {

    static ConfigComponent configComponent;

    public ConfigUtils(Context context, ConfigBiuder biuder) {
        configComponent = DaggerConfigComponent.builder()
                .configModule(new ConfigModule(context, biuder))
                .build();
    }

    public static Context getAppCtx() {
        return configComponent.getContext();
    }

    public static String getFilePath() {
        return configComponent.getConfigBiuder().filePath;
    }

    public static int getType() {
        return configComponent.getConfigBiuder().type;
    }

    public static boolean isDEBUG() {
        return configComponent.getConfigBiuder().DEBUG;
    }

    public static String getBaseUrl() {
        return configComponent.getConfigBiuder().BASE_URL;
    }
    public static String getTokenKey(){return configComponent.getConfigBiuder().token;}

    public static List<Interceptor> getInterceptor(){return configComponent.getConfigBiuder().interceptors;}

    public static String getCer() {
        return configComponent.getConfigBiuder().cer;
    }

    public static String getCerFileName() {
        return configComponent.getConfigBiuder().cerFileName;
    }

    public static int getTitleColor(){
        return configComponent.getConfigBiuder().titleColor;
    }

    public static int getBgColor(){
        return configComponent.getConfigBiuder().bgColor;
    }

    public static boolean isTitleCenter(){
        return configComponent.getConfigBiuder().isTitleCenter;
    }

    public static int getBackImg(){
        return configComponent.getConfigBiuder().backImg;
    }


    public static class ConfigBiuder {
        /** 是否  DEBUG 环境*/
        boolean DEBUG;
        /** 网络请求 服务器地址 url */
        String BASE_URL = "";

        /** 应用 文件根目录 名称（文件夹） */
        String filePath = "";
        int type = 2;

        /** https 公钥证书字符串 */
        String cer = "";
        /** https 公钥证书 文件字符串（放在 assets 目录下） */
        String cerFileName = "";

        /** 标题栏背景色 */
        int bgColor;
        /** 标题是否居中 */
        boolean isTitleCenter;
        /** 标题字体颜色 */
        int titleColor;

        /** 标题栏返回按钮 图片 */
        int backImg;

        /** token key */
        String token = "X-Access-Token";
        /** token 拦截器 */
        List<Interceptor> interceptors  = new ArrayList<>();

        public ConfigBiuder setDEBUG(boolean DEBUG) {
            this.DEBUG = DEBUG;
            return this;
        }

        public ConfigBiuder setBASE_URL(String BASE_URL) {
            this.BASE_URL = BASE_URL;
            return this;
        }

        public ConfigBiuder setCerFileName(String cerFileName) {
            this.cerFileName = cerFileName == null ? "" : cerFileName;
            return this;
        }

        public ConfigBiuder setCer(String cer) {
            this.cer = cer;
            return this;
        }

        public ConfigBiuder setBgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public ConfigBiuder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public ConfigBiuder setTitleCenter(boolean titleCenter) {
            isTitleCenter = titleCenter;
            return this;
        }

        public ConfigBiuder setBackImg(int backImg) {
            this.backImg = backImg;
            return this;
        }

        public ConfigBiuder setBaseFile(String filePath, int type) {
            this.filePath = filePath == null ? "" : filePath;
            this.type = type;
            return this;
        }

        public ConfigBiuder setToken(String token) {
            this.token = token == null ? "" : token;
            return this;
        }

        public ConfigBiuder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public ConfigUtils create(Context context){
            return new ConfigUtils(context, this);
        }
    }
}
