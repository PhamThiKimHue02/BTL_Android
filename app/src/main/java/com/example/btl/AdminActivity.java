package com.example.btl;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AdminActivity extends AppCompatActivity {

    // Các thành phần giao diện
    private TextView txtTotalTasks, txtLateTasks, txtCompletedPercent;
    private EditText edtSearch;
    private Button btnFilter;
    private RecyclerView recyclerTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);  // Đảm bảo file layout có tên là activity_admin.xml

        // Ánh xạ view từ XML
        txtTotalTasks = findViewById(R.id.txtTotalTasks);
        txtLateTasks = findViewById(R.id.txtLateTasks);
        txtCompletedPercent = findViewById(R.id.txtCompletedPercent);
        edtSearch = findViewById(R.id.edtSearch);
        btnFilter = findViewById(R.id.btnFilter);
        recyclerTasks = findViewById(R.id.recyclerTasks);

        // (Chưa cần xử lý logic ở đây theo yêu cầu)
    }
}
