package com.example.btl.Admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class capnhatduan extends AppCompatActivity {

    private EditText edtName, edtDesc, edtMembers;
    private Button btnStart, btnEnd, btnUpdate, btnAddTask;
    private RadioGroup radioPriority;
    private LinearLayout taskListContainer;

    private final Calendar startCal = Calendar.getInstance();
    private final Calendar endCal = Calendar.getInstance();

    private String projectId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        edtName = findViewById(R.id.edtEditProjectName);
        edtDesc = findViewById(R.id.edtEditDescription);
        edtMembers = findViewById(R.id.edtEditMembers);
        btnStart = findViewById(R.id.btnEditStartDate);
        btnEnd = findViewById(R.id.btnEditEndDate);
        btnUpdate = findViewById(R.id.btnUpdateProject);
        btnAddTask = findViewById(R.id.btnAddTask);
        radioPriority = findViewById(R.id.radioEditPriority);
        taskListContainer = findViewById(R.id.taskListContainer);

        db = FirebaseFirestore.getInstance();

        projectId = getIntent().getStringExtra("projectId");
        if (projectId == null || projectId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy dự án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProjectDetails();
        loadTasksForProject();

        btnStart.setOnClickListener(v -> pickDate(true));
        btnEnd.setOnClickListener(v -> pickDate(false));
        btnUpdate.setOnClickListener(v -> updateProject());

        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(capnhatduan.this, ThemNhiemVuActivity.class);
            intent.putExtra("projectId", projectId);
            startActivity(intent);
        });
    }

    private void pickDate(boolean isStart) {
        Calendar cal = isStart ? startCal : endCal;
        new DatePickerDialog(this,
                (view, y, m, d) -> {
                    cal.set(y, m, d);
                    String dateStr = d + "/" + (m + 1) + "/" + y;
                    if (isStart) btnStart.setText(dateStr);
                    else btnEnd.setText(dateStr);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void loadProjectDetails() {
        db.collection("projects").document(projectId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Dự án không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    edtName.setText(doc.getString("name"));
                    edtDesc.setText(doc.getString("description"));
                    edtMembers.setText(doc.getString("members"));
                    btnStart.setText(doc.getString("startDate"));
                    btnEnd.setText(doc.getString("endDate"));

                    String prio = doc.getString("priority");
                    if ("Cao".equals(prio)) radioPriority.check(R.id.rbEditHigh);
                    else if ("Trung bình".equals(prio)) radioPriority.check(R.id.rbEditMedium);
                    else if ("Thấp".equals(prio)) radioPriority.check(R.id.rbEditLow);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải dữ liệu dự án", Toast.LENGTH_SHORT).show());
    }

    private void updateProject() {
        String name = edtName.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        String members = edtMembers.getText().toString().trim();
        String start = btnStart.getText().toString();
        String end = btnEnd.getText().toString();

        int checkedId = radioPriority.getCheckedRadioButtonId();
        String priority = "Không rõ";
        if (checkedId == R.id.rbEditHigh) priority = "Cao";
        else if (checkedId == R.id.rbEditMedium) priority = "Trung bình";
        else if (checkedId == R.id.rbEditLow) priority = "Thấp";

        Map<String, Object> updated = new HashMap<>();
        updated.put("name", name);
        updated.put("description", desc);
        updated.put("members", members);
        updated.put("startDate", start);
        updated.put("endDate", end);
        updated.put("priority", priority);

        db.collection("projects").document(projectId).update(updated)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "✅ Cập nhật dự án thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "❌ Cập nhật thất bại", Toast.LENGTH_SHORT).show());
    }

    private void loadTasksForProject() {
        taskListContainer.removeAllViews();

        db.collection("projects")
                .document(projectId)
                .collection("tasks")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        String name = doc.getString("name");
                        String desc = doc.getString("description");
                        String start = doc.getString("startDate");
                        String end = doc.getString("endDate");
                        String members = doc.getString("members");

                        LinearLayout wrapper = new LinearLayout(this);
                        wrapper.setOrientation(LinearLayout.VERTICAL);
                        wrapper.setPadding(20, 20, 20, 20);
                        wrapper.setBackgroundColor(Color.parseColor("#eeeeee"));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        lp.setMargins(0, 8, 0, 8);
                        wrapper.setLayoutParams(lp);

                        TextView tv = new TextView(this);
                        tv.setText("\uD83D\uDCCC " + name +
                                "\n\uD83D\uDCDD " + desc +
                                "\n\uD83D\uDCC5 " + start + " → " + end +
                                "\n\uD83D\uDC65 " + members);
                        tv.setTextSize(16);
                        tv.setMaxLines(Integer.MAX_VALUE);
                        tv.setSingleLine(false);

                        Button btnDelete = new Button(this);
                        btnDelete.setText("❌ XÓA");
                        btnDelete.setOnClickListener(v -> {
                            doc.getReference().delete().addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Đã xóa nhiệm vụ", Toast.LENGTH_SHORT).show();
                                taskListContainer.removeView(wrapper);
                            });
                        });

                        wrapper.addView(tv);
                        wrapper.addView(btnDelete);
                        taskListContainer.addView(wrapper);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksForProject();
    }
}
