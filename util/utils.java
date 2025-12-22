package com.example.chapter03.util;

import android.content.Context;

public class utils {
    public static int dp2px(Context context, float dpValue) {
        //获取当前手机的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        //将dp值转换为px值
        return (int) (dpValue * scale + 0.5f);
    }
}
