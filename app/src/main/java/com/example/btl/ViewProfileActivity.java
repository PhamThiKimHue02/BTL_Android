package com.example.btl;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmailDetail, tvPhone, tvGender, tvDob;
    private ImageView imageProfileDetail;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Ánh xạ các view
        tvFullName = findViewById(R.id.tvFullName);
        tvEmailDetail = findViewById(R.id.tvEmailDetail);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        tvDob = findViewById(R.id.tvDob);
        imageProfileDetail = findViewById(R.id.imageProfileDetail);
        btnBack = findViewById(R.id.btnBack);

        // Lấy user hiện tại
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            String userId = currentUser.getUid(); // Sử dụng UID để truy cập document


            tvEmailDetail.setText(email != null ? email : "Chưa có email");

            // Khởi tạo Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(userId);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String fullName = document.getString("fullName");
                            String phone = document.getString("mobile");
                            String gender = document.getString("gender");
                            String dob = document.getString("dob");

                            // Gán giá trị vào TextView
                            tvFullName.setText(fullName != null ? fullName : "Chưa cập nhật");
                            tvPhone.setText(phone != null ? phone : "Chưa cập nhật");
                            tvGender.setText(gender != null ? gender : "Chưa cập nhật");
                            tvDob.setText(dob != null ? dob : "Chưa cập nhật");

                            // Log để debug
                            Log.d("ViewProfile", "FullName: " + fullName + ", Phone: " + phone + ", Gender: " + gender + ", Dob: " + dob);
                        } else {
                            Toast.makeText(ViewProfileActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ViewProfileActivity.this, "Lỗi truy vấn dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ViewProfile", "Error: " + task.getException().getMessage());
                    }
                }
            });
        }

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());
    }
}