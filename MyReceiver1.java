package com.example.chapter03;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/*
    本广播接收者程序分别调用了应用的主Activity程序和播放音乐的服务程序
 */
public class MyReceiver1 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //启动service1
        Intent intent1 = new Intent(context, MyService1.class);
        //在广播组件里，通过上下文对象启动音乐播放服务组件
        context.startService(intent1);
        //新建调用主Activity组件的意图

    }
}