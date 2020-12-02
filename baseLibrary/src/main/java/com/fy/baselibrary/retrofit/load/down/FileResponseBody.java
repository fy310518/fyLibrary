package com.fy.baselibrary.retrofit.load.down;

import com.fy.baselibrary.retrofit.interceptor.FileDownInterceptor;
import com.fy.baselibrary.utils.imgload.imgprogress.ProgressListener;
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
 * describe: 文件下载 计算进度 ResponseBody
 * Created by fangs on 2019/10/8 21:21.
 */
public class FileResponseBody extends ResponseBody {
    private static final String TAG = "FileResponseBody";
    /**
     * 文件请求url
     */
    private String requestUrl;

    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;

    private ProgressListener listener;

    public FileResponseBody(ResponseBody responseBody, String requestUrl) {
        this.responseBody = responseBody;
        this.requestUrl = requestUrl;

        listener = FileDownInterceptor.LISTENER_MAP.get(requestUrl);
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
            bufferedSource = Okio.buffer(new ProgressSource(responseBody.source()));
        }
        return bufferedSource;
    }

    private class ProgressSource extends ForwardingSource {
        private long fullLength;
        private long totalBytesRead = 0L;
        private int currentProgress;

        ProgressSource(Source source) {
            super(source);
            fullLength = responseBody.contentLength();
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);

            if (bytesRead == -1) {
                totalBytesRead = fullLength;
            } else {
                totalBytesRead += bytesRead;
            }

            int progress = (int) (100f * totalBytesRead / fullLength);
            L.e(TAG, "download progress is " + progress);

            if (listener != null && progress != currentProgress) {
                listener.onProgress(progress);
            }
            if (listener != null && totalBytesRead == fullLength) {
                listener = null;
            }

            currentProgress = progress;
            return bytesRead;
        }
    }

    public String getRequestUrl() {
        return requestUrl == null ? "" : requestUrl;
    }
}