package com.fy.baselibrary.utils.media;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * 递归实现扫描某个目录
 * Created by fangs on 2018/1/15.
 */
public class MediaScanner {

    private static final String TAG = MediaScanner.class.getSimpleName();

    private MediaScannerConnection mConn = null;
    private SannerClient mClient = null;
    private File mFile = null;
    private String mMimeType = null;

    public MediaScanner(Context context) {
        if (mClient == null) {
            mClient = new SannerClient();
        }
        if (mConn == null) {
            mConn = new MediaScannerConnection(context, mClient);
        }
    }

    public void scanFile(File file, String mimeType) {
        mFile = file;
        mMimeType = mimeType;
        mConn.connect();
    }

    class SannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

        @Override
        public void onMediaScannerConnected() {
            if (mFile == null) {
                return;
            }
            scan(mFile, mMimeType);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mConn.disconnect();
        }


        private void scan(File file, String type) {
            if (file.isFile()) {
                mConn.scanFile(file.getAbsolutePath(), null);
                return;
            }
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : file.listFiles()) {
                scan(f, type);
            }
        }
    }
}
