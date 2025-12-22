package com.example.chapter03;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity1020 extends AppCompatActivity {


    private final Fragment fragment1 = new Fragment1();
    private final Fragment fragment2 = new Fragment2();
    private final Fragment fragment3 = new Fragment3();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1020);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setItemIconTintList(null);

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(fragment1);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_wechat) {
                replaceFragment(fragment1);
                return true;
            } else if (itemId == R.id.navigation_contacts) {
                replaceFragment(fragment2);
                return true;
            } else if (itemId == R.id.navigation_me) {
                replaceFragment(fragment3);
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);


        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
