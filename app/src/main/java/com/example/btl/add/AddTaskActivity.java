package com.example.btl.add;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtProjectName, edtDescription, edtMember;
    private RadioGroup radioPriority;
    private Button btnStartDate, btnEndDate, btnAddMember, btnCreateProject;
    private LinearLayout memberContainer;

    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        edtProjectName = findViewById(R.id.edtProjectName);
        edtDescription = findViewById(R.id.edtDescription);
        edtMember = findViewById(R.id.edtMember);
        radioPriority = findViewById(R.id.radioPriority);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnAddMember = findViewById(R.id.btnAddMember);
        btnCreateProject = findViewById(R.id.btnCreateProject);
        memberContainer = findViewById(R.id.memberContainer);

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

        btnAddMember.setOnClickListener(v -> {
            String name = edtMember.getText().toString().trim();
            if (!name.isEmpty()) {
                addMemberView(name);
                edtMember.setText("");
            }
        });

        btnCreateProject.setOnClickListener(v -> {
            final String name = edtProjectName.getText().toString().trim();
            final String desc = edtDescription.getText().toString().trim();
            final String startDate = btnStartDate.getText().toString();
            final String endDate = btnEndDate.getText().toString();

            int priorityId = radioPriority.getCheckedRadioButtonId();
            final String priority;
            if (priorityId == R.id.rbHigh) priority = "Cao";
            else if (priorityId == R.id.rbMedium) priority = "Trung bình";
            else if (priorityId == R.id.rbLow) priority = "Thấp";
            else priority = "Không rõ";

            final StringBuilder members = new StringBuilder();
            for (int i = 0; i < memberContainer.getChildCount(); i++) {
                LinearLayout row = (LinearLayout) memberContainer.getChildAt(i);
                TextView tv = (TextView) row.getChildAt(0);
                members.append(tv.getText().toString()).append(", ");
            }
            if (members.length() > 2) {
                members.setLength(members.length() - 2);
            }

            Map<String, Object> project = new HashMap<>();
            project.put("name", name);
            project.put("description", desc);
            project.put("startDate", startDate);
            project.put("endDate", endDate);
            project.put("priority", priority);
            project.put("members", members.toString());

            db.collection("projects")
                    .add(project)
                    .addOnSuccessListener(documentReference -> {
                        String projectId = documentReference.getId(); // ✅ lấy ID

                        Intent intent = new Intent();
                        intent.putExtra("projectId", projectId);
                        intent.putExtra("projectName", name);
                        intent.putExtra("description", desc);
                        intent.putExtra("startDate", startDate);
                        intent.putExtra("endDate", endDate);
                        intent.putExtra("priority", priority);
                        intent.putExtra("members", members.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // TODO: thêm Toast nếu lưu thất bại
                    });
        });
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String dateStr = dayOfMonth + "/" + (month + 1) + "/" + year;
                    if (isStartDate) {
                        btnStartDate.setText(dateStr);
                    } else {
                        btnEndDate.setText(dateStr);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void addMemberView(String name) {
        LinearLayout memberRow = new LinearLayout(this);
        memberRow.setOrientation(LinearLayout.HORIZONTAL);
        memberRow.setGravity(Gravity.CENTER_VERTICAL);
        memberRow.setPadding(8, 8, 8, 8);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextSize(16);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button btnRemove = new Button(this);
        btnRemove.setText("XÓA");
        btnRemove.setOnClickListener(v -> memberContainer.removeView(memberRow));

        memberRow.addView(tvName);
        memberRow.addView(btnRemove);

        memberContainer.addView(memberRow);
    }
}
