package com.fy.baselibrary.retrofit.load.down;

import android.support.annotation.NonNull;

import com.fy.baselibrary.utils.notify.L;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * describe: 文件下载 ResponseBody
 * Created by fangs on 2019/10/8 21:21.
 */
public class FileResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public FileResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
        L.e("文件长度", responseBody.contentLength() + "---文件下载---" + Thread.currentThread().getName());
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }


    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(getSource(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source getSource(Source source) {
        return new ForwardingSource(source) {
            long downloadBytes = 0L;

            @Override
            public long read(@NonNull Buffer buffer, long byteCount) throws IOException {
                long singleRead = super.read(buffer, byteCount);
                if (-1 != singleRead) {
                    downloadBytes += singleRead;
                }
                return singleRead;
            }
        };
    }
}