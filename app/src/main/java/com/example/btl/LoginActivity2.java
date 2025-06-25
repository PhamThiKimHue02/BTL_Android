package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity2 extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        TextView tvForgot = findViewById(R.id.tv_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Phân quyền
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");
                                            if ("admin".equals(role)) {
                                                Toast.makeText(this, "Đăng nhập trang quản lý thành công!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, AdminActivity.class));
                                            } else {
                                                Toast.makeText(this, "Đăng nhập trang nhân viên thành công!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, UserActivity.class));
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Không tìm thấy thông tin nhân viên.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Lỗi truy vấn dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnSignup.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        tvForgot.setOnClickListener(view ->
                startActivity(new Intent(this, ForgotActivity.class))
        );
    }
}

