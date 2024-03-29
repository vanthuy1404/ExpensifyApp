package com.example.expensify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Fragment addFragment, tradeFragment, reportFragment, userFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        addFragment = new AddFragment();
        tradeFragment = new TradeFragment();
        reportFragment = new ReportFragment();
        userFragment = new UserFragment();

        // Mặc định hiển thị fragment "Thêm giao dịch"
        loadFragment(addFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.add) {
                    loadFragment(addFragment);
                    return true;
                } else if (itemId == R.id.trade) {
                    loadFragment(tradeFragment);
                    return true;
                } else if (itemId == R.id.user) {
                    loadFragment(userFragment);
                    return true;
                } else if (itemId == R.id.report) {
                    loadFragment(reportFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // Chuyển đổi fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Frame_layout, fragment);
        fragmentTransaction.commit();
    }
}