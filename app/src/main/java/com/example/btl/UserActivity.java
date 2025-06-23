package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private LinearLayout projectListLayout;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        projectListLayout = findViewById(R.id.project_list);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity2.class));
            finish();
            return;
        }

        currentUserEmail = user.getEmail();
        loadUserProjects();
    }

    private void loadUserProjects() {
        FirebaseFirestore.getInstance()
                .collection("projects")
                .get()
                .addOnSuccessListener(projects -> {
                    projectListLayout.removeAllViews();
                    for (QueryDocumentSnapshot project : projects) {
                        Object membersObj = project.get("members");
                        if (membersObj instanceof List) {
                            List<String> members = (List<String>) membersObj;

                            if (members.contains(currentUserEmail)) {
                                String projectId = project.getId();
                                String name = project.getString("name");
                                String description = project.getString("description");
                                String endDate = project.getString("endDate");

                                CardView card = new CardView(this);
                                card.setRadius(16);
                                card.setCardElevation(8);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, 0, 0, 24);
                                card.setLayoutParams(params);

                                LinearLayout contentLayout = new LinearLayout(this);
                                contentLayout.setOrientation(LinearLayout.VERTICAL);
                                contentLayout.setPadding(32, 32, 32, 32);

                                TextView tvTitle = new TextView(this);
                                tvTitle.setText(name);
                                tvTitle.setTextSize(18);
                                tvTitle.setTextColor(getResources().getColor(android.R.color.black));
                                tvTitle.setPadding(0, 0, 0, 8);

                                TextView tvDesc = new TextView(this);
                                tvDesc.setText("Mô tả: " + description);

                                TextView tvEnd = new TextView(this);
                                tvEnd.setText("Kết thúc: " + endDate);

                                Button btnUserFinish = new Button(this);
                                btnUserFinish.setText("Tôi đã hoàn thành tất cả công việc");
                                btnUserFinish.setVisibility(View.GONE); // Ẩn mặc định

                                // Kiểm tra trạng thái đã hoàn thành từ Firestore
                                FirebaseFirestore.getInstance()
                                        .collection("projects")
                                        .document(projectId)
                                        .collection("userStatus")
                                        .document(currentUserEmail)
                                        .get()
                                        .addOnSuccessListener(statusDoc -> {
                                            Boolean completed = statusDoc.getBoolean("completed");
                                            if (completed == null || !completed) {
                                                btnUserFinish.setVisibility(View.VISIBLE);
                                            }
                                        });

                                btnUserFinish.setOnClickListener(v -> {
                                    FirebaseFirestore.getInstance()
                                            .collection("projects")
                                            .document(projectId)
                                            .collection("userStatus")
                                            .document(currentUserEmail)
                                            .set(new HashMap<String, Object>() {{
                                                put("completed", true);
                                            }})
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Bạn đã đánh dấu hoàn thành công việc.", Toast.LENGTH_SHORT).show();
                                                btnUserFinish.setVisibility(View.GONE);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Lỗi khi cập nhật trạng thái.", Toast.LENGTH_SHORT).show();
                                            });
                                });

                                contentLayout.addView(tvTitle);
                                contentLayout.addView(tvDesc);
                                contentLayout.addView(tvEnd);
                                contentLayout.addView(btnUserFinish);

                                card.addView(contentLayout);
                                projectListLayout.addView(card);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải dự án.", Toast.LENGTH_SHORT).show());
        // Điều hướng sang Profile
        LinearLayout navAccount = findViewById(R.id.nav_account);
        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
