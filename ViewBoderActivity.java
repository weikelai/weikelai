package com.example.chapter03;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter03.util.utils;

public class ViewBoderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_boder);
        TextView tv_code = findViewById(R.id.tv_code);
        //获取tv_code的布局参数（包含宽度和高度）
        ViewGroup.LayoutParams layoutParams = tv_code.getLayoutParams();
        //修改布局参数的宽度数值，默认是px单位，需要把dp转换成px
        layoutParams.width = utils.dp2px(this, 300);
        //设置修改后的布局参数
        tv_code.setLayoutParams(layoutParams);
    }
}