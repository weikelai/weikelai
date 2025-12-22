//package com.example.chapter03;
//
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//public class MainActivity1021 extends AppCompatActivity implements View.OnClickListener {
//
//    private Fragment f1, f2, f3;
//    private TextView tv1, tv2, tv3;
//    private FragmentManager fm;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main1021);
//
//        // 初始化视图
//        tv1 = findViewById(R.id.tvfragment1);
//        tv2 = findViewById(R.id.tvfragment2);
//        tv3 = findViewById(R.id.tvfragment3);
//
//        // 设置点击监听
//        tv1.setOnClickListener(this);
//        tv2.setOnClickListener(this);
//        tv3.setOnClickListener(this);
//
//        // 获取FragmentManager
//        fm = getSupportFragmentManager();
//
//        // 设置默认选中的页面 - 关键：在添加Fragment之前调用
//        // 这里我们先模拟点击第一个标签，来初始化状态
//        onClick(tv1);
//    }
//
//    @Override
//    public void onClick(View v) {
//        FragmentTransaction ft = fm.beginTransaction();
//
//        // 先隐藏所有Fragment（如果它们已经被添加的话）
//        if (f1 != null && f1.isAdded()) ft.hide(f1);
//        if (f2 != null && f2.isAdded()) ft.hide(f2);
//        if (f3 != null && f3.isAdded()) ft.hide(f3);
//
//        // 根据点击的View来决定显示哪个Fragment
//        int viewId = v.getId();
//        if (viewId == R.id.tvfragment1) {
//            if (f1 == null) {
//                f1 = new Fragment1();
//                ft.add(R.id.frame, f1);
//            } else {
//                ft.show(f1);
//            }
//            updateTabAppearance(tv1);
//        } else if (viewId == R.id.tvfragment2) {
//            if (f2 == null) {
//                f2 = new Fragment2();
//                ft.add(R.id.frame, f2);
//            } else {
//                ft.show(f2);
//            }
//            updateTabAppearance(tv2);
//        } else if (viewId == R.id.tvfragment3) {
//            if (f3 == null) {
//                f3 = new Fragment3();
//                ft.add(R.id.frame, f3);
//            } else {
//                ft.show(f3);
//            }
//            updateTabAppearance(tv3);
//        }
//
//        ft.commit();
//    }
//
//    // 更新底部标签的外观
//    private void updateTabAppearance(TextView selectedTab) {
//        // 重置所有标签的样式
//        tv1.setTextColor(Color.BLACK);
//        tv1.setTypeface(Typeface.DEFAULT);
//        tv2.setTextColor(Color.BLACK);
//        tv2.setTypeface(Typeface.DEFAULT);
//        tv3.setTextColor(Color.BLACK);
//        tv3.setTypeface(Typeface.DEFAULT);
//
//        // 设置选中标签的样式
//        selectedTab.setTextColor(Color.BLUE);
//        selectedTab.setTypeface(Typeface.DEFAULT_BOLD);
//    }
//}
package com.example.chapter03;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity1021 extends AppCompatActivity implements View.OnClickListener {

    FragmentManager fm;
    Fragment f1,f2,f3;
    TextView tv1,tv2,tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main1021);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mbutton1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        f1= new Fragment1();
        f2= new Fragment2();
        f3=new Fragment3();


        tv1=findViewById(R.id.tvfragment1);
        tv2=findViewById(R.id.tvfragment2);
        tv3=findViewById(R.id.tvfragment3);

        fm=getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frame,f1)
                .add(R.id.frame,f2)
                .add(R.id.frame,f3)
                .commit();
        hide();
        // 关键：设置点击监听器
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);

        // 关键：使用 androidx.fragment.app.FragmentManager
        fm = getSupportFragmentManager();

    }
    private void hide(){
        FragmentTransaction ft=fm.beginTransaction();
        ft.hide(f1)
                .hide(f2)
                .hide(f3)
                .commit();
        // 设置默认显示的页面和标签状态，通过模拟点击第一个标签来统一初始化流程
        onClick(tv1);
    }
//我使用的if-else，老师使用swich
    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fm.beginTransaction();

        // 先隐藏所有Fragment（如果它们已经被添加的话）
        if (f1 != null && f1.isAdded()) ft.hide(f1);
        if (f2 != null && f2.isAdded()) ft.hide(f2);
        if (f3 != null && f3.isAdded()) ft.hide(f3);

        // 根据点击的View来决定显示哪个Fragment
        int viewId = v.getId();
        if (viewId == R.id.tvfragment1) {
            if (f1 == null) {
                f1 = new Fragment1();
                ft.add(R.id.frame, f1);
            } else {
                ft.show(f1);
            }
        } else if (viewId == R.id.tvfragment2) {
            if (f2 == null) {
                f2 = new Fragment2();
                ft.add(R.id.frame, f2);
            } else {
                ft.show(f2);
            }
        } else if (viewId == R.id.tvfragment3) {
            if (f3 == null) {
                f3 = new Fragment3();
                ft.add(R.id.frame, f3);
            } else {
                ft.show(f3);
            }
        }

        ft.commit();
    }
}