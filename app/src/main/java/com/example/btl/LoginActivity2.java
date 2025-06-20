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

    private final String ADMIN_EMAIL = "admin@gmail.com";
    private final String ADMIN_PASS = "123456";

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

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu là admin cứng thì không kiểm tra Firebase Auth
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASS)) {
                Toast.makeText(this, "Đăng nhập admin thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminActivity.class));
                finish();
                return;
            }

            // Nếu là người dùng thường
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");
                                            if ("admin".equals(role)) {
                                                Toast.makeText(this, "Chào Admin!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, AdminActivity.class));
                                            } else {
                                                Toast.makeText(this, "Chào người dùng!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, UserActivity.class));
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
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

