package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class LoginActivity1 extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);
        TextView tvForgot = findViewById(R.id.textView2); // ánh xạ TextView "Forgot Password?"

        // Đăng nhập
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity1.this, LoginActivity2.class);
            startActivity(intent);
        });

        // Đăng ký
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity1.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Quên mật khẩu
        tvForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity1.this, ForgotActivity.class);
            startActivity(intent);
        });

    }
}
