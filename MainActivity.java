package com.example.chapter03;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    Button Button1;
    TextView textView6;
    TextView textView5;
    TextView textView4;
    RatingBar ratingBar;
    CalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mbutton1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button1 = findViewById(R.id.button);
        textView6=findViewById(R.id.textView6);
        textView5=findViewById(R.id.textView5);
        textView4=findViewById(R.id.textView4);

        ratingBar=findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                textView5.setText("你给的好评是: "+rating+" 星");
            }
        });
        calendarView=findViewById(R.id.calendarView2);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                textView4.setText("你选择的日期是: "+year+"年"+(month + 1)+"月"+dayOfMonth+"日");
            }
        });
        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView6.setText("你点击了按钮");
                Button1.setText("你点击了按钮");
            }
        });
        Log.d("MainActivity", "onCreate: ");
    }
}
