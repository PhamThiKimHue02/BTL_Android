package com.example.btl.Admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final TaskListFragment taskListFragment = new TaskListFragment();  // Tab 1
    private final FragmentCuaToiAdmin cuaToiFragment = new FragmentCuaToiAdmin(); // Tab 2

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return taskListFragment;
        } else {
            return cuaToiFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public TaskListFragment getTaskListFragment() {
        return taskListFragment;
    }
}