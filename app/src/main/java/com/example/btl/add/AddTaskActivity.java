package com.example.btl.add;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtProjectName, edtDescription, edtMember;
    private RadioGroup radioPriority;
    private Button btnStartDate, btnEndDate, btnAddMember, btnCreateProject;
    private LinearLayout memberContainer;

    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();

    private FirebaseFirestore db;
    private final List<String> memberEmails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
//ánh xạ giao diện, kết nối đến xml
        edtProjectName = findViewById(R.id.edtProjectName);
        edtDescription = findViewById(R.id.edtDescription);
        edtMember = findViewById(R.id.edtMember);
        radioPriority = findViewById(R.id.radioPriority);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnAddMember = findViewById(R.id.btnAddMember);
        btnCreateProject = findViewById(R.id.btnCreateProject);
        memberContainer = findViewById(R.id.memberContainer);
//chọn ngày bắt đầu/kết thúc
        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));
//kỉểm tra tên thành viên xem có rỗng không
        btnAddMember.setOnClickListener(v -> {
            String name = edtMember.getText().toString().trim();

            if (name.isEmpty()) {
                edtMember.setError("Vui lòng nhập tên thành viên!");
                return;
            }
//tìm user bằng fullname
            db.collection("users")
                    .whereEqualTo("fullName", name)
                    .get()
                    .addOnSuccessListener(query -> {
                        if (!query.isEmpty())
                        //lấy email,thêm vào danh sách và hiển thị lên giao diện
                        {
                            String email = query.getDocuments().get(0).getString("email");

                            if (email != null && !memberEmails.contains(email)) {
                                memberEmails.add(email);
                                addMemberView(name, email);
                                edtMember.setText("");
                                Toast.makeText(this, "Đã thêm: " + name, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Thành viên đã được thêm!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            edtMember.setError("Không tìm thấy người dùng tên '" + name + "'");
                        }
                    })
                    .addOnFailureListener(e -> edtMember.setError("Lỗi khi tìm người dùng!"));
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

            if (memberEmails.isEmpty()) {
                Toast.makeText(this, "Vui lòng thêm ít nhất một thành viên!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> project = new HashMap<>();
            project.put("name", name);
            project.put("description", desc);
            project.put("startDate", startDate);
            project.put("endDate", endDate);
            project.put("priority", priority);
            project.put("members", new ArrayList<>(memberEmails));

            db.collection("projects")
                    .add(project)
                    .addOnSuccessListener(documentReference -> {
                        Intent intent = new Intent();
                        intent.putExtra("projectId", documentReference.getId());
                        intent.putExtra("projectName", name);
                        intent.putExtra("description", desc);
                        intent.putExtra("startDate", startDate);
                        intent.putExtra("endDate", endDate);
                        intent.putExtra("priority", priority);
                        intent.putExtra("members", String.join("\n", memberEmails));
                        setResult(RESULT_OK, intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lưu dự án thất bại!", Toast.LENGTH_SHORT).show());
        });
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
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

    private void addMemberView(String name, String email) {
        LinearLayout memberRow = new LinearLayout(this);
        memberRow.setOrientation(LinearLayout.VERTICAL);
        memberRow.setPadding(8, 8, 8, 8);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextSize(16);
        tvName.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvEmail = new TextView(this);
        tvEmail.setText(email);
        tvEmail.setTextSize(14);
        tvEmail.setTextColor(getResources().getColor(android.R.color.darker_gray));

        Button btnRemove = new Button(this);
        btnRemove.setText("Xóa");
        btnRemove.setOnClickListener(v -> {
            memberContainer.removeView(memberRow);
            memberEmails.remove(email);
        });

        memberRow.addView(tvName);
        memberRow.addView(tvEmail);
        memberRow.addView(btnRemove);

        memberContainer.addView(memberRow);
    }
}