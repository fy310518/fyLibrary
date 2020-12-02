package com.fy.baselibrary.retrofit.converter.file;

import android.annotation.SuppressLint;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.load.LoadOnSubscribe;
import com.fy.baselibrary.retrofit.load.down.FileResponseBody;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.cache.SpfAgent;
import com.fy.baselibrary.utils.imgload.imgprogress.ProgressListener;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * describe: 文件下载 转换器
 * Created by fangs on 2019/8/28 22:03.
 */
public class FileResponseBodyConverter implements Converter<ResponseBody, File> {

    public static final Map<String, LoadOnSubscribe> LISTENER_MAP = new HashMap<>();

    //添加 进度发射器
    public static void addListener(String url, LoadOnSubscribe loadOnSubscribe) {
        LISTENER_MAP.put(url, loadOnSubscribe);
    }

    //取消注册下载监听
    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }

    @Override
    public File convert(ResponseBody responseBody) throws IOException {
        String requestUrl = null;
        try {
            //使用反射获得我们自定义的response
            Class aClass = responseBody.getClass();
            Field field = aClass.getDeclaredField("delegate");
            field.setAccessible(true);
            ResponseBody body = (ResponseBody) field.get(responseBody);
            if (body instanceof FileResponseBody) {
                FileResponseBody fileResponseBody = (FileResponseBody) body;
                requestUrl = fileResponseBody.getRequestUrl();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        final String filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();
        return saveFile(LISTENER_MAP.get(requestUrl), responseBody, requestUrl, filePath);
    }


    /**
     * 根据ResponseBody 写文件
     * @param responseBody
     * @param url
     * @param filePath   文件保存路径
     * @return
     */
    public static File saveFile(LoadOnSubscribe loadOnSubscribe, final ResponseBody responseBody, String url, final String filePath) {
        final File tempFile = FileUtils.createTempFile(url, filePath);

        File file = null;
        try {
            file = writeFileToDisk(loadOnSubscribe, responseBody, tempFile.getAbsolutePath());

            int FileDownStatus = SpfAgent.init("").getInt(file.getName() + Constant.FileDownStatus);
            if (FileDownStatus == 4) {
                boolean renameSuccess = FileUtils.reNameFile(url, tempFile.getPath());
                return FileUtils.getFile(url, filePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            loadOnSubscribe.onError(e);
        }

        return file;
    }

    /**
     * 单线程 断点下载
     * @param loadOnSubscribe
     * @param responseBody
     * @param filePath
     * @return
     * @throws IOException
     */
    @SuppressLint("DefaultLocale")
    public static File writeFileToDisk(LoadOnSubscribe loadOnSubscribe, ResponseBody responseBody, String filePath) throws Exception {
        long totalByte = responseBody.contentLength();
        L.e("fy_file_FileDownInterceptor", "文件下载 写数据" + "---" + Thread.currentThread().getName());

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } else {
            if (null != loadOnSubscribe){
                loadOnSubscribe.setmSumLength(file.length() + totalByte);
                loadOnSubscribe.onRead(file.length());
            }
        }


        SpfAgent.init("").saveInt(file.getName() + Constant.FileDownStatus, 1).commit(false);//正在下载

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        long tempFileLen = file.length();
        randomAccessFile.seek(tempFileLen);

        byte[] buffer = new byte[1024 * 4];
        InputStream is = responseBody.byteStream();

        long downloadByte = 0;
        while (true) {
            int len = is.read(buffer);
            if (len == -1) {//下载完成
                if (null != loadOnSubscribe) loadOnSubscribe.clean();

                SpfAgent.init("").saveInt(file.getName() + Constant.FileDownStatus, 4).commit(false);//下载完成
                break;
            }

            int FileDownStatus = SpfAgent.init("").getInt(file.getName() + Constant.FileDownStatus);
            if (FileDownStatus == 2 || FileDownStatus == 3) break;//暂停或者取消 停止下载

            randomAccessFile.write(buffer, 0, len);
            downloadByte += len;

            if (null != loadOnSubscribe) loadOnSubscribe.onRead(len);
        }

        is.close();
        randomAccessFile.close();
        responseBody.close();

        return file;
    }

}
