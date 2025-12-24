package com.example.chapter03;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ServiceActivity extends AppCompatActivity {
    private static final String TAG = "ServiceActivity";
    Button button1,button2,button3,button4;

    // 已删除 music1，保留 6 首
    int[] songs={
            R.raw.night,
            R.raw.weather,
            R.raw.block,
            R.raw.unique,
            R.raw.us,
            R.raw.yanhuo
    };

    MyService2.Mybinder binder;
    BroadcastReceiver receiver;
    private ServiceConnection connection; // 提升为成员变量
    // 用于存储当前选择的歌曲资源ID
    private int selectedSong;
    private boolean isBound = false;
    
    // 歌单视图数组
    private TextView[] songViews;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service);
        Log.d(TAG, "onCreate");

        button1 = findViewById(R.id.mbutton);
        button2 = findViewById(R.id.mbutton2);
        button3 = findViewById(R.id.mbutton3);
        button4 = findViewById(R.id.mbutton4);

        // 初始化按钮状态
        button2.setEnabled(false);
        button4.setEnabled(false);
        
        // 初始化歌单
        initSongList();

        // 请求短信接收权限，请求码为 1
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECEIVE_SMS"}, 100);

        // 注册广播接收器
        receiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 收到短信时，创建启动 MyService1 的 Intent
                Intent serviceIntent = new Intent(context, MyService1.class);
                context.startService(serviceIntent);
            }
        };
        // 创建 IntentFilter，指定要接收的广播是短信接收
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, intentFilter);

        // 创建 Intent，用于启动服务
        Intent intent1 = new Intent(this, MyService1.class);
        Intent intent2 = new Intent(this, MyService2.class);
        
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSong == 0) {
                    Toast.makeText(ServiceActivity.this, "请先选择一首音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent1.putExtra("song", selectedSong);
                startService(intent1);
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(intent1);
                button1.setEnabled(true);
                button2.setEnabled(false);
            }
        });

        // ServiceConnection 用于处理与服务的连接和断开
        connection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected");
                binder = (MyService2.Mybinder) service;
                // 只有在选择了歌曲的情况下才播放
                if (selectedSong != 0) {
                    binder.playSong(selectedSong);
                }
                isBound = true;
                button3.setEnabled(false);
                button4.setEnabled(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
                binder = null;
                isBound = false;
                button3.setEnabled(true);
                button4.setEnabled(false);
            }
        };

        // 点击“绑定”按钮
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查是否已选择歌曲
                if (selectedSong == 0) {
                    Toast.makeText(ServiceActivity.this, "请先选择一首音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 绑定服务
                bindService(intent2,connection,BIND_AUTO_CREATE);
            }
        });

        // 点击“解绑”按钮
        button4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 如果已绑定，则解绑
                if (isBound) {
                    unbindService(connection);
                    isBound = false;
                    // 清除选择，防止下次直接播放
                    // selectedSong = 0; 
                    button3.setEnabled(true);
                    button4.setEnabled(false);
                }
            }
        });
    }
    
    private void initSongList() {
        // 更新为 6 首歌
        songViews = new TextView[6];
        songViews[0] = findViewById(R.id.tv_song_0);
        songViews[1] = findViewById(R.id.tv_song_1);
        songViews[2] = findViewById(R.id.tv_song_2);
        songViews[3] = findViewById(R.id.tv_song_3);
        songViews[4] = findViewById(R.id.tv_song_4);
        songViews[5] = findViewById(R.id.tv_song_5);

        String[] songNames = {
                "夜未央", "天气预报", "消散对白",
                "唯一", "我们俩", "化作烟火为你坠落"
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < songViews.length; i++) {
                    if (v.getId() == songViews[i].getId()) {
                        selectedSong = songs[i];
                        Toast.makeText(ServiceActivity.this, "已选择: " + songNames[i], Toast.LENGTH_SHORT).show();
                        // 选中变色
                        songViews[i].setTextColor(Color.BLUE);
                    } else {
                        // 未选中恢复默认颜色
                        songViews[i].setTextColor(Color.parseColor("#000000")); // Black
                    }
                }
            }
        };

        for (TextView tv : songViews) {
            tv.setOnClickListener(listener);
            // 初始化颜色
            tv.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 在 Activity 不可见时（如按Back键）自动解绑
        // 确保触发 Service 的 onUnbind 和 onDestroy，从而停止音乐
        if (isBound) {
            unbindService(connection);
            isBound = false;
            Log.d(TAG, "onStop: Unbinding service");
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        // Activity 销毁时注销广播接收器，防止内存泄漏
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        // 防止 ServiceConnection 泄漏
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    // 权限请求结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"权限申请成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"权限申请失败",Toast.LENGTH_SHORT).show();
        }
    }
}