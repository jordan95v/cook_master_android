package com.jordan.cook_master_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FormationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navbar = findViewById(R.id.bottom_navigation);
        navbar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.formations) {
                return true;
            } else if (item.getItemId() == R.id.courses) {
                return true;
            } else if (item.getItemId() == R.id.user_profile) {
                return true;
            }
            return true;
        });
    }
}