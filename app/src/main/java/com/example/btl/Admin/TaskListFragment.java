package com.example.btl.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListFragment extends Fragment {

    private LinearLayout projectContainer;

    public TaskListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cong_viec_nhom, container, false);
        projectContainer = view.findViewById(R.id.projectContainer);

        loadAllProjectsFromFirebase();

        return view;
    }

    public void addProject(String name, String desc, String start, String end, String priority,
                           List<String> membersList, String projectId) {

        if (projectContainer == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 16, 0, 16);

        TextView txtTrangThai = new TextView(getContext());
        txtTrangThai.setTextSize(16);
        txtTrangThai.setTextColor(0xFF4CAF50);
        txtTrangThai.setVisibility(View.GONE);

        TextView txtCanhBao = new TextView(getContext());
        txtCanhBao.setTextSize(14);
        txtCanhBao.setVisibility(View.GONE);

        Button btnHoanThanh = new Button(getContext());
        btnHoanThanh.setText("Xác nhận hoàn thành dự án");
        btnHoanThanh.setVisibility(View.GONE);

        Button btn = new Button(getContext());
        btn.setText("Tên dự án: " + name + "\n" +
                "Mô tả: " + desc + "\n" +
                "Bắt đầu: " + start + " | Kết thúc: " + end + "\n" +
                "Ưu tiên: " + priority + "\n" +
                "Thành viên: " + String.join(", ", membersList));
        btn.setAllCaps(false);
        btn.setPadding(24, 24, 24, 24);
        btn.setBackgroundResource(android.R.drawable.btn_default);
        btn.setTextSize(16);
        btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        btn.setOnClickListener(v -> {
            if (projectId != null && !projectId.isEmpty()) {
                Intent intent = new Intent(getContext(), capnhatduan.class);
                intent.putExtra("projectId", projectId);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID dự án!", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnDelete = new Button(getContext());
        btnDelete.setText("XÓA");
        btnDelete.setAllCaps(false);
        btnDelete.setTextSize(14);
        btnDelete.setPadding(16, 8, 16, 8);
        btnDelete.setBackgroundColor(0xFFE57373);
        btnDelete.setTextColor(0xFFFFFFFF);

        btnDelete.setOnClickListener(v -> {
            db.collection("projects")
                    .document(projectId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        projectContainer.removeView(layout);
                        Toast.makeText(getContext(), "Đã xóa dự án", Toast.LENGTH_SHORT).show();
                    });
        });

        db.collection("projects").document(projectId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    String status = snapshot.getString("status");
                    if ("Đã hoàn thành và xác nhận".equals(status)) {
                        projectContainer.removeView(layout);
                        return;
                    }

                    db.collection("projects").document(projectId)
                            .collection("userStatus")
                            .get()
                            .addOnSuccessListener(userStatuses -> {
                                boolean allCompleted = true;

                                for (String email : membersList) {
                                    boolean found = false;
                                    for (DocumentSnapshot d : userStatuses) {
                                        if (email.equals(d.getId())) {
                                            found = true;
                                            Boolean completed = d.getBoolean("completed");
                                            if (!Boolean.TRUE.equals(completed)) {
                                                allCompleted = false;
                                            }
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        allCompleted = false;
                                        break;
                                    }
                                }

                                if (allCompleted) {
                                    txtTrangThai.setVisibility(View.GONE);
                                    btnHoanThanh.setVisibility(View.VISIBLE);
                                } else {
                                    txtTrangThai.setText("Đang chờ thành viên hoàn tất...");
                                    txtTrangThai.setTextColor(0xFF000000);
                                    txtTrangThai.setVisibility(View.VISIBLE);
                                    btnHoanThanh.setVisibility(View.GONE);
                                }
                            });
                });

        btnHoanThanh.setOnClickListener(v -> {
            db.collection("projects").document(projectId)
                    .update("status", "Đã hoàn thành và xác nhận")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Dự án đã được xác nhận hoàn thành", Toast.LENGTH_SHORT).show();
                        projectContainer.removeView(layout);
                    });
        });

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date ngayKetThuc = sdf.parse(end);
            Date ngayHienTai = new Date();
            long millisDiff = ngayKetThuc.getTime() - ngayHienTai.getTime();
            long daysLeft = millisDiff / (1000 * 60 * 60 * 24);

            if (daysLeft < 0) {
                txtCanhBao.setText("Dự án đã quá hạn!");
                txtCanhBao.setTextColor(0xFFFFC107);
                txtCanhBao.setVisibility(View.VISIBLE);
            } else if (daysLeft <= 3) {
                txtCanhBao.setText("Dự án sắp đến hạn!");
                txtCanhBao.setTextColor(0xFFFF0000);
                txtCanhBao.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        layout.addView(txtTrangThai);
        layout.addView(txtCanhBao);
        layout.addView(btnHoanThanh);
        layout.addView(btn);
        layout.addView(btnDelete);

        projectContainer.addView(layout);
    }

    public void loadAllProjectsFromFirebase() {
        if (projectContainer != null) {
            projectContainer.removeAllViews();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("projects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String status = doc.getString("status");
                        if ("Đã hoàn thành và xác nhận".equals(status)) continue;

                        String name = doc.getString("name");
                        String desc = doc.getString("description");
                        String start = doc.getString("startDate");
                        String end = doc.getString("endDate");
                        String priority = doc.getString("priority");

                        List<String> membersList;
                        Object membersObj = doc.get("members");

                        if (membersObj instanceof List) {
                            membersList = (List<String>) membersObj;
                        } else {
                            membersList = new java.util.ArrayList<>();
                        }

                        String projectId = doc.getId();

                        addProject(name, desc, start, end, priority, membersList, projectId);
                    }
                });
    }
}