package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class UserActivity extends AppCompatActivity {

    private LinearLayout projectListLayout;
    private TextView tabAll, tabMine;
    private LinearLayout navHome, navTask, navAccount;

    private FirebaseFirestore db;
    private String currentEmail = "", currentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        db = FirebaseFirestore.getInstance();

        // ánh xạ
        projectListLayout = findViewById(R.id.project_list);
        tabAll = findViewById(R.id.tab_all_projects);
        tabMine = findViewById(R.id.tab_my_projects);
        navHome = findViewById(R.id.nav_home);
        navTask = findViewById(R.id.nav_task);
        navAccount = findViewById(R.id.nav_account);

        // Tabs
        tabAll.setOnClickListener(v -> {
            setTabHighlight(true);
            loadProjects(false);
        });

        tabMine.setOnClickListener(v -> {
            setTabHighlight(false);
            loadProjects(true);
        });

        // Navigation (hiện tại chỉ Toast để test)
        navHome.setOnClickListener(v -> {
            Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show();
        });

        navTask.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            // điều hươg sang trang profile
        });

        navAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            // startActivity(new Intent(this, AccountActivity.class));
        });

        // Lấy user hiện tại
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        currentEmail = doc.getString("email");
                        currentName = doc.getString("fullName");
                        loadProjects(false);
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setTabHighlight(boolean isAll) {
        tabAll.setTextColor(isAll ? 0xFF00C5C2 : 0xFF999999);
        tabMine.setTextColor(isAll ? 0xFF999999 : 0xFF00C5C2);
    }

    private void loadProjects(boolean onlyMine) {
        db.collection("projects").get()
                .addOnSuccessListener(query -> {
                    projectListLayout.removeAllViews();

                    for (QueryDocumentSnapshot doc : query) {
                        // Handle members field safely - it might be stored as String or List
                        List<String> members = null;
                        Object membersObj = doc.get("members");

                        if (membersObj instanceof List) {
                            members = (List<String>) membersObj;
                        } else if (membersObj instanceof String) {
                            // If members is stored as a comma-separated string, split it
                            String membersStr = (String) membersObj;
                            if (membersStr != null && !membersStr.trim().isEmpty()) {
                                members = java.util.Arrays.asList(membersStr.split(","));
                            }
                        }

                        // Check if user should see this project
                        if (onlyMine && (members == null || (!members.contains(currentEmail) && !members.contains(currentName)))) {
                            continue;
                        }

                        LinearLayout card = new LinearLayout(this);
                        card.setOrientation(LinearLayout.VERTICAL);
                        card.setPadding(24, 24, 24, 24);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, 32);
                        card.setLayoutParams(params);
                        card.setBackgroundColor(0xFFFFFFFF);

                        TextView tvTitle = new TextView(this);
                        String projectName = doc.getString("name");
                        tvTitle.setText("📌 " + (projectName != null ? projectName : "Không có tên"));
                        tvTitle.setTextSize(18);
                        tvTitle.setTextColor(0xFF000000);
                        tvTitle.setPadding(0, 0, 0, 8);
                        card.addView(tvTitle);

                        TextView tvDesc = new TextView(this);
                        String description = doc.getString("description");
                        tvDesc.setText("📄 " + (description != null ? description : "Không có mô tả"));
                        card.addView(tvDesc);

                        TextView tvDate = new TextView(this);
                        String startDate = doc.getString("startDate");
                        String endDate = doc.getString("endDate");
                        tvDate.setText("📅 " + (startDate != null ? startDate : "N/A") + " - " + (endDate != null ? endDate : "N/A"));
                        card.addView(tvDate);

                        TextView tvPriority = new TextView(this);
                        String priority = doc.getString("priority");
                        tvPriority.setText("⚠️ Ưu tiên: " + (priority != null ? priority : "Không xác định"));
                        card.addView(tvPriority);

                        TextView tvMembers = new TextView(this);
                        if (members != null && !members.isEmpty()) {
                            tvMembers.setText("👥 " + String.join(", ", members));
                        } else {
                            tvMembers.setText("👥 Không có thành viên");
                        }
                        tvMembers.setTextColor(0xFF555555);
                        tvMembers.setPadding(0, 8, 0, 0);
                        card.addView(tvMembers);

                        projectListLayout.addView(card);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải danh sách dự án: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
