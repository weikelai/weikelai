package com.example.chapter03;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService2 extends Service {
    private static final String TAG = "MyService2";
    MediaPlayer mediaPlayer;
    private final IBinder binder = new Mybinder();

    public MyService2() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate executed");
        // 服务创建时不自动播放音乐
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy executed");
        // 服务销毁时（如解绑后），彻底停止音乐并释放资源
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind executed");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind executed");
        // 返回 false，表示默认情况，如果再次绑定，会调用 onBind 而不是 onRebind
        // 这也是确保服务在所有客户端解绑后能被系统销毁的条件之一
        return false; 
    }

    public class Mybinder extends Binder {
        // 供 Activity 调用的方法，用于播放指定的歌曲
        public void playSong(int songId) {
            Log.d(TAG, "playSong executed");
            // 如果当前有音乐在播放，则先停止并释放资源
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            // 创建新的 MediaPlayer 实例并开始播放
            mediaPlayer = MediaPlayer.create(getApplicationContext(), songId);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }
}

