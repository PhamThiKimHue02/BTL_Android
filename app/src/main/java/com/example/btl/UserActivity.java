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

    // Giao diện chứa danh sách dự án
    private LinearLayout projectListLayout;

    // Email người dùng hiện tại để lọc các dự án có liên quan
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user); // Gán layout giao diện

        // Liên kết với layout LinearLayout dùng để hiển thị danh sách dự án
        projectListLayout = findViewById(R.id.project_list);

        // Kiểm tra người dùng đã đăng nhập hay chưa
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity2.class)); // Chuyển về màn hình đăng nhập
            finish();
            return;
        }

        // Lưu email người dùng để đối chiếu
        currentUserEmail = user.getEmail();

        // Tải danh sách dự án của người dùng
        loadUserProjects();
    }

    private void loadUserProjects() {
        FirebaseFirestore.getInstance()
                .collection("projects") // Lấy tất cả dự án từ Firestore
                .get()
                .addOnSuccessListener(projects -> {
                    projectListLayout.removeAllViews(); // Xóa các dự án cũ

                    for (QueryDocumentSnapshot project : projects) {
                        Object membersObj = project.get("members");
                        if (membersObj instanceof List) {
                            List<String> members = (List<String>) membersObj;

                            // Kiểm tra người dùng hiện tại có nằm trong danh sách thành viên không
                            if (members.contains(currentUserEmail)) {
                                String projectId = project.getId(); // ID của dự án
                                String name = project.getString("name"); // Tên dự án
                                String description = project.getString("description"); // Mô tả
                                String endDate = project.getString("endDate"); // Ngày kết thúc

                                // Tạo một CardView hiển thị dự án
                                CardView card = new CardView(this);
                                card.setRadius(16); // Bo góc
                                card.setCardElevation(8); // Đổ bóng

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, 0, 0, 24);
                                card.setLayoutParams(params);

                                // Bên trong CardView là LinearLayout dọc
                                LinearLayout contentLayout = new LinearLayout(this);
                                contentLayout.setOrientation(LinearLayout.VERTICAL);
                                contentLayout.setPadding(32, 32, 32, 32);

                                // Tên dự án
                                TextView tvTitle = new TextView(this);
                                tvTitle.setText(name);
                                tvTitle.setTextSize(18);
                                tvTitle.setTextColor(getResources().getColor(android.R.color.black));
                                tvTitle.setPadding(0, 0, 0, 8);

                                // Mô tả
                                TextView tvDesc = new TextView(this);
                                tvDesc.setText("Mô tả: " + description);

                                // Ngày kết thúc
                                TextView tvEnd = new TextView(this);
                                tvEnd.setText("Kết thúc: " + endDate);

                                // Nút đánh dấu hoàn thành
                                Button btnUserFinish = new Button(this);
                                btnUserFinish.setText("Tôi đã hoàn thành tất cả công việc");
                                btnUserFinish.setVisibility(View.GONE); // Ẩn nút lúc đầu

                                // Kiểm tra nếu user đã hoàn thành thì ẩn nút
                                FirebaseFirestore.getInstance()
                                        .collection("projects")
                                        .document(projectId)
                                        .collection("userStatus")
                                        .document(currentUserEmail)
                                        .get()
                                        .addOnSuccessListener(statusDoc -> {
                                            Boolean completed = statusDoc.getBoolean("completed");
                                            if (completed == null || !completed) {
                                                btnUserFinish.setVisibility(View.VISIBLE); // Hiện nút nếu chưa hoàn thành
                                            }
                                        });

                                // Khi bấm nút "Tôi đã hoàn thành"
                                btnUserFinish.setOnClickListener(v -> {
                                    FirebaseFirestore.getInstance()
                                            .collection("projects")
                                            .document(projectId)
                                            .collection("userStatus")
                                            .document(currentUserEmail)
                                            .set(new HashMap<String, Object>() {{
                                                put("completed", true); // Ghi trạng thái đã hoàn thành
                                            }})
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Bạn đã đánh dấu hoàn thành công việc.", Toast.LENGTH_SHORT).show();
                                                btnUserFinish.setVisibility(View.GONE); // Ẩn nút sau khi hoàn thành
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Lỗi khi cập nhật trạng thái.", Toast.LENGTH_SHORT).show();
                                            });
                                });

                                // Gắn các view con vào nội dung CardView
                                contentLayout.addView(tvTitle);
                                contentLayout.addView(tvDesc);
                                contentLayout.addView(tvEnd);
                                contentLayout.addView(btnUserFinish);

                                // Thêm content vào CardView và CardView vào danh sách
                                card.addView(contentLayout);
                                projectListLayout.addView(card);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải dự án.", Toast.LENGTH_SHORT).show());

        // Xử lý điều hướng sang màn hình "Tài khoản"
        LinearLayout navAccount = findViewById(R.id.nav_account);
        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
