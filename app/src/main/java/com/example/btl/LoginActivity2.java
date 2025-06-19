package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity2 extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        // Ánh xạ các thành phần
        etEmail = findViewById(R.id.et_email);           // EditText nhập email
        etPassword = findViewById(R.id.et_pass);     // EditText nhập password
        btnLogin = findViewById(R.id.btn_login);         // Nút Đăng nhập
        btnSignup = findViewById(R.id.btn_signup);       // Nút Đăng ký
        TextView tvForgot = findViewById(R.id.tv_forgot_password);  // TextView Quên mật khẩu


        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity2.this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity2.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity2.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity2.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Xử lý đăng ký
        btnSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity2.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Quên mật khẩu
        tvForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity2.this, ForgotActivity.class);
            startActivity(intent);
        });

    }
}
