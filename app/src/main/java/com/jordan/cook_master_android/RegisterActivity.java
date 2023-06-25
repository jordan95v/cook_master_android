package com.jordan.cook_master_android;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            this.finish();
        });
    }
}