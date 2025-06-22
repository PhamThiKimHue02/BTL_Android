package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btl.Admin.TaskListFragment;
import com.example.btl.Admin.ViewPagerAdapter;
import com.example.btl.add.AddTaskActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fab;
    private ViewPagerAdapter adapter;

    private final ActivityResultLauncher<Intent> addTaskLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String name = result.getData().getStringExtra("projectName");
                    String desc = result.getData().getStringExtra("description");
                    String start = result.getData().getStringExtra("startDate");
                    String end = result.getData().getStringExtra("endDate");
                    String priority = result.getData().getStringExtra("priority");
                    String members = result.getData().getStringExtra("members");
                    String projectId = result.getData().getStringExtra("projectId");

                    TaskListFragment fragment = adapter.getTaskListFragment();
                    if (fragment != null) {
                        fragment.addProject(name, desc, start, end, priority, members, projectId);
                    }

                    Toast.makeText(this, "Đã tạo dự án thành công", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Ánh xạ
        tabLayout = findViewById(R.id.tabLayoutAdmin);
        viewPager = findViewById(R.id.viewPagerAdmin);
        fab = findViewById(R.id.fabAddTask);

        // Adapter
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Tab + ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("CÔNG VIỆC NHÓM");
            else tab.setText("CỦA TÔI");
        }).attach();

        // Nút tạo dự án
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddTaskActivity.class);
            addTaskLauncher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskListFragment fragment = adapter.getTaskListFragment();
        if (fragment != null) {
            fragment.loadAllProjectsFromFirebase();
        }
    }
}