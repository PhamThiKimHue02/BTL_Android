package com.example.btl.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TaskListFragment extends Fragment {

    private LinearLayout projectContainer;

    public TaskListFragment() {
        // Constructor rỗng là bắt buộc
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cong_viec_nhom, container, false);
        projectContainer = view.findViewById(R.id.projectContainer);
        return view;
    }

    // ✅ Hiển thị một dự án dưới dạng Button + XÓA
    public void addProject(String name, String desc, String start, String end, String priority, String members, String projectId) {
        if (projectContainer == null) return;

        // Layout chứa nút và nút xoá
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 16, 0, 16);

        Button btn = new Button(getContext());
        btn.setText("📌 " + name + "\n" +
                "🔸 Mô tả: " + desc + "\n" +
                "📅 Bắt đầu: " + start + " | Kết thúc: " + end + "\n" +
                "⚡ Ưu tiên: " + priority + "\n" +
                "👥 Thành viên: " + members);
        btn.setAllCaps(false);
        btn.setPadding(24, 24, 24, 24);
        btn.setBackgroundResource(android.R.drawable.btn_default);
        btn.setTextSize(16);
        btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        // Khi nhấn → mở màn hình capnhatduan
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), capnhatduan.class);
            intent.putExtra("projectId", projectId);
            startActivity(intent);
        });

        // Nút xoá dự án
        Button btnDelete = new Button(getContext());
        btnDelete.setText("❌ XÓA");
        btnDelete.setAllCaps(false);
        btnDelete.setTextSize(14);
        btnDelete.setPadding(16, 8, 16, 8);
        btnDelete.setBackgroundColor(0xFFE57373);
        btnDelete.setTextColor(0xFFFFFFFF);

        btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("projects")
                    .document(projectId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        projectContainer.removeView(layout);
                    });
        });

        layout.addView(btn);
        layout.addView(btnDelete);

        projectContainer.addView(layout);
    }

    // ✅ Load toàn bộ dự án từ Firestore
    public void loadAllProjectsFromFirebase() {
        if (projectContainer != null) {
            projectContainer.removeAllViews();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("projects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String desc = doc.getString("description");
                        String start = doc.getString("startDate");
                        String end = doc.getString("endDate");
                        String priority = doc.getString("priority");
                        String members = doc.getString("members");
                        String projectId = doc.getId();

                        addProject(name, desc, start, end, priority, members, projectId);
                    }
                });
    }
}
