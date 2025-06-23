package com.example.btl.Admin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FragmentCuaToiAdmin extends Fragment {

    private LinearLayout layoutCuaToi;

    public FragmentCuaToiAdmin() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cua_toi_admin, container, false);
        layoutCuaToi = view.findViewById(R.id.layoutCuaToi);

        loadCompletedProjects();

        return view;
    }

    private void loadCompletedProjects() {
        layoutCuaToi.removeAllViews();

        FirebaseFirestore.getInstance()
                .collection("projects")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    for (DocumentSnapshot doc : querySnapshots) {
                        String status = doc.getString("status");

                        if ("Đã hoàn thành và xác nhận".equals(status)) {
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

                            LinearLayout cardLayout = new LinearLayout(getContext());
                            cardLayout.setOrientation(LinearLayout.VERTICAL);
                            cardLayout.setPadding(24, 24, 24, 24);
                            cardLayout.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 24);
                            cardLayout.setLayoutParams(params);

                            TextView tvInfo = new TextView(getContext());
                            tvInfo.setText("Tên dự án: " + name + "\n" +
                                    "Mô tả: " + desc + "\n" +
                                    "Bắt đầu: " + start + " | Kết thúc: " + end + "\n" +
                                    "Ưu tiên: " + priority + "\n" +
                                    "Thành viên: " + String.join(", ", membersList));
                            tvInfo.setTextSize(16);
                            tvInfo.setGravity(Gravity.START);

                            TextView tvStatus = new TextView(getContext());
                            tvStatus.setText("Trạng thái: Đã hoàn thành và xác nhận");
                            tvStatus.setTextSize(15);
                            tvStatus.setTextColor(0xFF4CAF50); // xanh lá

                            cardLayout.addView(tvInfo);
                            cardLayout.addView(tvStatus);

                            layoutCuaToi.addView(cardLayout);
                        }
                    }
                });
    }
}