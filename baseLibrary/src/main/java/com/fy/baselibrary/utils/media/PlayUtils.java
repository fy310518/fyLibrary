package com.fy.baselibrary.utils.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;

import com.fy.baselibrary.application.ioc.ConfigUtils;

/**
 * 音频播放 工具类
 * Created by fangs on 2018/1/15.
 */
public class PlayUtils {

    private volatile static PlayUtils instance = null;
    private MediaPlayer mMediaPlayer;
    private boolean isPause;
    private AnimationDrawable animation;//播放语音的 帧动画

    public static synchronized PlayUtils getInstance() {
        if (null == instance) {
            synchronized (PlayUtils.class) {
                if (null == instance) {
                    instance = new PlayUtils();
                }
            }
        }

        return instance;
    }

    private PlayUtils() {}

    /**
     * 播放 URI 音乐
     * @param uri
     * @param onCompletionListener
     */
    public void playSound(Context context, Uri uri, MediaPlayer.OnCompletionListener onCompletionListener) {
        initMediaPlayer();

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(context, uri);
//            mMediaPlayer.prepare();
//            mMediaPlayer.start();
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playAnimation();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放 url 音乐（本地文件 OR 网络地址）
     * @param url
     * @param onCompletionListener
     */
    public void playAsyncSound(String url, MediaPlayer.OnCompletionListener onCompletionListener) {
        initMediaPlayer();

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playAnimation();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * 获取当前音频的播放时长
     * @return
     */
    public float getAudioLength(String filePath) {
        float duration = 0;
        if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            long size = mMediaPlayer.getDuration();
            duration = size / 1000;
            //设置文件时长，单位 "分:秒" 格式
            String total = duration / 60 + ":" + duration % 60;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mMediaPlayer.release();//释放media资源
            mMediaPlayer = null;
        }
        return duration;
    }

    /**
     * 判断播放状态
     *
     * @return 是否播放中
     */
    public boolean isPlay() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 当前是isPause状态
     */
    public void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        closeAnimation();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        closeAnimation();
    }

    //关闭动画
    private void closeAnimation(){
        if (null != animation) {
            animation.selectDrawable(animation.getNumberOfFrames() - 1);//设置到最后帧动画
            animation.stop();
            animation = null;
        }
    }

    //播放动画
    private void playAnimation(){
        if (null != animation) animation.start();
    }

    //设置动画
    public void setAnimation(AnimationDrawable animation) {
        this.animation = animation;
    }

    //初始化 MediaPlayer
    private void initMediaPlayer() {
        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
    }


    /****************************************************************/
    /**
     * 振动
     * isCustom 是否自定义振动
     * rhythm   振动节奏 new long[]{500, 200, 500, 200}
     */
    @SuppressLint("MissingPermission")
    public static void vibrator(boolean isCustom, long[] rhythm){
        Context context = ConfigUtils.getAppCtx();
        // 获取震动服务对象
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (!isCustom){
            // 只震动 150 ms，一次
            vibrator.vibrate(150);
        } else {
            //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
            vibrator.vibrate(rhythm, -1);
        }
    }

    /**
     * 取消振动
     */
    @SuppressLint("MissingPermission")
    public static void cancelVibrator(){
        Context context = ConfigUtils.getAppCtx();
        // 获取震动服务对象
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
    }
}
