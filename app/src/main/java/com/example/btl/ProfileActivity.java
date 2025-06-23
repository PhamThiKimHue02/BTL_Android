package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView tvUsername, tvEmail;
    private Button btnEditProfile, btnViewInfo, btnChangePassword, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Gắn layout profile.xml

        // Ánh xạ các thành phần giao diện
        imageProfile = findViewById(R.id.imageProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnViewInfo = findViewById(R.id.btnViewInfo);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            tvEmail.setText(user.getEmail());
            tvUsername.setText(user.getDisplayName() != null ? user.getDisplayName() : "Người dùng");
        }
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("fullName", tvUsername.getText().toString());
            //nút edit profile
            startActivity(intent);
        });
        btnViewInfo.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ViewProfileActivity.class);
            startActivity(intent);
        });
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditPassActivity.class);
            startActivity(intent);
        });
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity1.class);
            startActivity(intent);
        });
    }
}
