package com.fy.baselibrary.application.ioc;

import android.content.Context;
import android.text.TextUtils;

import com.fy.baselibrary.dress.DressColor;
import com.fy.baselibrary.statuslayout.OnStatusAdapter;

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

    public static boolean isFontDefault() {
        return configComponent.getConfigBiuder().isFontDefault;
    }

    public static boolean isEnableCacheInterceptor() {
        return configComponent.getConfigBiuder().isEnableCacheInterceptor;
    }

    public static String getBaseUrl() {
        return configComponent.getConfigBiuder().BASE_URL;
    }

    public static String getAddCookieKey(){return configComponent.getConfigBiuder().addCookieKey;}
    public static String getCookieDataKey(){return configComponent.getConfigBiuder().cookieData;}

    public static String getTokenKey(){return configComponent.getConfigBiuder().token;}

    public static List<Interceptor> getInterceptor(){return configComponent.getConfigBiuder().interceptors;}

    public static OnStatusAdapter getOnStatusAdapter(){return configComponent.getConfigBiuder().statusAdapter;}

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
        /** 是否  跟随系统字体大小 默认跟随*/
        boolean isFontDefault = true;

        /** 应用 文件根目录 名称（文件夹） */
        String filePath = "";
        int type = 0;

        /** 标题栏背景色 */
        int bgColor;
        /** 标题是否居中 */
        boolean isTitleCenter;
        /** 标题字体颜色 */
        int titleColor;
        /** 标题栏 返回按钮 图片 */
        int backImg;

        /** 网络请求 服务器地址 url */
        String BASE_URL = "";
        /** https 公钥证书字符串 */
        String cer = "";
        /** https 公钥证书 文件字符串（放在 assets 目录下） */
        String cerFileName = "";

        /** 是否  启用缓存拦截器 */
        boolean isEnableCacheInterceptor;
        /** 向网络请求 添加 cookie 数据 的 key */
        String addCookieKey = "cookie";
        /** 从网络请求 响应数据中拿到返回的 cookie数据 的 key */
        String cookieData = "set-cookie";

        /** token key */
        String token = "X-Access-Token";
        /** 添加 自定义拦截器；如：token 拦截器 */
        List<Interceptor> interceptors  = new ArrayList<>();

        /** 添加 自定义 色彩处理 对象【相当于 给app 穿衣服，从而改变app 界面 展示样式】*/
        List<DressColor> dressColors  = new ArrayList<>();

        /** 多状态布局 适配器 */
        OnStatusAdapter statusAdapter;

        public ConfigBiuder setDEBUG(boolean DEBUG) {
            this.DEBUG = DEBUG;
            return this;
        }

        public ConfigBiuder setFontDefault(boolean fontDefault) {
            isFontDefault = fontDefault;
            return this;
        }

        public ConfigBiuder setEnableCacheInterceptor(boolean enableCacheInterceptor) {
            isEnableCacheInterceptor = enableCacheInterceptor;
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

        public ConfigBiuder setCookie(String addCookieKey, String cookieData) {
            this.addCookieKey = TextUtils.isEmpty(addCookieKey) ? "" : addCookieKey;
            this.cookieData = TextUtils.isEmpty(cookieData) ? "" : cookieData;
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

        public ConfigBiuder setStatusAdapter(OnStatusAdapter statusAdapter) {
            this.statusAdapter = statusAdapter;
            return this;
        }

        public ConfigUtils create(Context context){
            return new ConfigUtils(context, this);
        }
    }
}
