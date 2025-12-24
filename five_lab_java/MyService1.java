package com.example.chapter03;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

// MyService1 是一个 Service，用于在后台播放音乐。这是一个“启动服务”（Started Service）。
public class MyService1 extends Service {
    private static final String TAG = "MyService1";
    // MediaPlayer 对象，用于播放音频
    MediaPlayer mediaPlayer;

    public MyService1() {

    }

    // 服务首次创建时调用
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate executed");
    }

    // 每次通过 startService() 启动服务时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed");
        // 从 Intent 中获取要播放的歌曲资源 ID，如果未提供，则默认为 0
        int song = intent.getIntExtra("song", 0);

        // 如果当前有音乐正在播放，则先停止
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // 只有当提供了有效的歌曲 ID 时才播放
        if (song != 0) {
            // 根据歌曲资源 ID 创建一个新的 MediaPlayer 实例
            mediaPlayer = MediaPlayer.create(this, song);
            if (mediaPlayer != null) {
                // 开始播放音乐
                mediaPlayer.start();
            } else {
                Log.e(TAG, "Failed to create MediaPlayer with song ID: " + song);
            }
        } else {
            Log.w(TAG, "No valid song ID provided");
        }

        // 返回值决定了如果服务在执行完毕后被系统杀死，将如何处理
        // super.onStartCommand 默认返回 START_STICKY
        return super.onStartCommand(intent, flags, startId);
    }

    // 当服务被销毁时调用
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy executed");
        // 停止音乐播放
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); // 释放 MediaPlayer 资源
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    // 对于启动服务，此方法通常返回 null，因为我们不与它绑定
    @Override
    public IBinder onBind(Intent intent) {
        // 这个服务不支持绑定，所以抛出异常或返回 null
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
