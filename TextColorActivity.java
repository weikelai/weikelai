package com.example.chapter03;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class TextColorActivity extends AppCompatActivity {

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_text_color);
        //从布局文件中获取名为tp_code_system的TextView
        TextView tp_code_system = findViewById(R.id.tp_code_system);
        //设置tp_code_system的文字颜色为红色
        tp_code_system.setTextColor(Color.RED);
        //从布局文件中获取名为tp_code_eight的TextView
        TextView tp_code_eight = findViewById(R.id.tp_code_eight);
        //设置tp_code_eight的文字颜色为绿色
        tp_code_eight.setTextColor(0xff00ff00);
        //从布局文件中获取名为tp_code_six的TextView
        TextView tp_code_six = findViewById(R.id.tp_code_six);
        //设置tp_code_six的文字颜色为蓝色
        tp_code_six.setTextColor(0xff0000ff);
        //从布局文件中获取名为tp_code_backgroud的TextView
        TextView tp_code_backgroud = findViewById(R.id.tp_code_backgroud);
        //设置tp_code_backgroud的背景颜色为绿色
        //tp_code_backgroud.setBackgroundColor(Color.GREEN);
        //颜色来自资源文件colors.xml
        tp_code_backgroud.setBackgroundColor(R.color.magenta);
    }
}