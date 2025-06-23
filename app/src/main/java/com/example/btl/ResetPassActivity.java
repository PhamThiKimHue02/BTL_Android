package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.button.MaterialButton;

public class ResetPassActivity extends AppCompatActivity {

    private TextView tvSendStatus;
    private FirebaseAuth mAuth;
    private MaterialButton btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        tvSendStatus = findViewById(R.id.tvSendStatus);
        mAuth = FirebaseAuth.getInstance();
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Lấy email từ ForgotActivity
        String email = getIntent().getStringExtra("email");

        if (email != null && !email.isEmpty()) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> {
                        tvSendStatus.setText("Đã gửi email đặt lại mật khẩu đến: " + email);
                    })
                    .addOnFailureListener(e -> {
                        tvSendStatus.setText("Gửi email thất bại: " + e.getMessage());
                        Toast.makeText(ResetPassActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            tvSendStatus.setText("Không nhận được địa chỉ email.");
            Toast.makeText(this, "Email không hợp lệ.", Toast.LENGTH_SHORT).show();
        }


        btnBackToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(ResetPassActivity.this, LoginActivity2.class);
            startActivity(intent);
            finish();
        });
    }
}