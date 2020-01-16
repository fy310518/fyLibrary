package com.fy.baselibrary.retrofit.load.up;

import com.fy.baselibrary.retrofit.load.LoadOnSubscribe;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * describe: 上传文件 计算上传进度 请求体
 * Created by fangs on 2018/11/12.
 */
public class FileProgressRequestBody extends RequestBody {

    protected File file;
    protected String contentType;
    protected LoadOnSubscribe subscribe;

    protected FileProgressRequestBody() {}

    public FileProgressRequestBody(File file, String contentType, LoadOnSubscribe subscribe) {
        this.file = file;
        this.contentType = contentType;
        this.subscribe = subscribe;
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    private int sum;//todo 加这个参数 是因为不同的手机系统，这个回调方法执行次数不同原因未知（手里有的一个华为手机、华为平板执行3次，三星执行两次）
    public static final int SEGMENT_SIZE = 2048;
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sum++;
        boolean ispercent = sink instanceof Buffer;//如果传入的 sink 为 Buffer 类型，则直接写入，不进行百分比统计
        L.e("进度--", ispercent + "----");

        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;

            long read;
            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();

                if (!ispercent && sum > 1){
//                    String percent = TransfmtUtils.doubleToKeepTwoDecimalPlaces(100d * total / contentLength());
//                    percent = percent.equals("100.0") ? "----100" : percent;
//                    L.e("进度R", percent + "%-->" + file.getName() + "-->" + Thread.currentThread().getId());

                    if (null != subscribe)subscribe.onRead(read);
                }
            }
        } finally {
            Util.closeQuietly(source);
        }
    }

}
