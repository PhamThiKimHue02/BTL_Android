package com.example.btl.Admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class ThemNhiemVuActivity extends AppCompatActivity {

    private EditText edtTaskName, edtTaskDescription, edtTaskMember;
    private Button btnTaskStartDate, btnTaskEndDate, btnAddTaskMember, btnCreateTask;
    private LinearLayout taskMemberContainer;

    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();

    private List<String> memberList = new ArrayList<>();
    private FirebaseFirestore db;
    private String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhiem_vu);

        edtTaskName = findViewById(R.id.edtTaskName);
        edtTaskDescription = findViewById(R.id.edtTaskDescription);
        edtTaskMember = findViewById(R.id.edtTaskMember);
        btnTaskStartDate = findViewById(R.id.btnTaskStartDate);
        btnTaskEndDate = findViewById(R.id.btnTaskEndDate);
        btnAddTaskMember = findViewById(R.id.btnAddTaskMember);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        taskMemberContainer = findViewById(R.id.taskMemberContainer);

        db = FirebaseFirestore.getInstance();
        projectId = getIntent().getStringExtra("projectId");

        if (projectId == null || projectId.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin dự án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnTaskStartDate.setOnClickListener(v -> pickDate(true));
        btnTaskEndDate.setOnClickListener(v -> pickDate(false));

        btnAddTaskMember.setOnClickListener(v -> {
            String member = edtTaskMember.getText().toString().trim();
            if (!member.isEmpty()) {
                memberList.add(member);
                addMemberToUI(member);
                edtTaskMember.setText("");
            }
        });

        btnCreateTask.setOnClickListener(v -> createTask());
    }

    private void pickDate(boolean isStart) {
        Calendar cal = isStart ? startCal : endCal;
        new DatePickerDialog(this,
                (view, y, m, d) -> {
                    cal.set(y, m, d);
                    String dateStr = d + "/" + (m + 1) + "/" + y;
                    if (isStart) btnTaskStartDate.setText(dateStr);
                    else btnTaskEndDate.setText(dateStr);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void addMemberToUI(String member) {
        TextView tv = new TextView(this);
        tv.setText("👤 " + member);
        tv.setPadding(12, 12, 12, 12);
        tv.setTextSize(16);
        taskMemberContainer.addView(tv);
    }

    private void createTask() {
        String name = edtTaskName.getText().toString().trim();
        String desc = edtTaskDescription.getText().toString().trim();
        String startDate = btnTaskStartDate.getText().toString();
        String endDate = btnTaskEndDate.getText().toString();

        if (name.isEmpty() || desc.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || memberList.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> task = new HashMap<>();
        task.put("name", name);
        task.put("description", desc);
        task.put("startDate", startDate);
        task.put("endDate", endDate);
        task.put("members", String.join(", ", memberList)); // Ghép chuỗi tên

        db.collection("projects")
                .document(projectId)
                .collection("tasks")
                .add(task)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Tạo nhiệm vụ thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tạo nhiệm vụ", Toast.LENGTH_SHORT).show();
                });
    }
}