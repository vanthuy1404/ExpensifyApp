package com.example.expensify;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensify.R;
import com.example.expensify.ViewPagerAdapter;

public class ReportFragment extends Fragment {

    private ViewPager2 mViewPager;

    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_report_fragment, container, false);

        // Ánh xạ ViewPager2 từ layout
        mViewPager = rootView.findViewById(R.id.viewPager);

        // Khởi tạo và thiết lập Adapter cho ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity());
        mViewPager.setAdapter(adapter);

        return rootView;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Màn hình được xoay ngang
            // Xử lý các thay đổi cần thiết ở đây
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Màn hình được xoay dọc
            // Xử lý các thay đổi cần thiết ở đây
        }
    }
}