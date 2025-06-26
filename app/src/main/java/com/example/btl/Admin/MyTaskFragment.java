package com.example.btl.Admin;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyTaskFragment extends Fragment {

    public MyTaskFragment() {
        // Bắt buộc có constructor rỗng
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Hiển thị nội dung đơn giản
        TextView textView = new TextView(getContext());
        textView.setText("Danh sách công việc của tôi");
        textView.setTextSize(20);
        textView.setPadding(40, 60, 40, 60);

        return textView;
    }
}