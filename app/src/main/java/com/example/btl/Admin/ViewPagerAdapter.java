package com.example.btl.Admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final TaskListFragment taskListFragment = new TaskListFragment();  // Giữ nguyên 1 instance
    private final MyTaskFragment myTaskFragment = new MyTaskFragment();        // Nếu cần giữ cả 2 tab

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return taskListFragment;
        } else {
            return myTaskFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    // Cho phép AdminActivity gọi để load lại dữ liệu
    public TaskListFragment getTaskListFragment() {
        return taskListFragment;
    }
}