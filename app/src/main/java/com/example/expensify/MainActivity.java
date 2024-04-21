package com.example.expensify;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment addFragment, tradeFragment, reportFragment, userFragment;
    private SharedPreferences pref;
    private static final int ADD_FRAGMENT_ID = R.id.add;
    private static final int TRADE_FRAGMENT_ID = R.id.trade;
    private static final int USER_FRAGMENT_ID = R.id.user;
    private static final int REPORT_FRAGMENT_ID = R.id.report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        pref = getPreferences(MODE_PRIVATE);


        addFragment = new AddFragment();
        tradeFragment = new TradeFragment();
        reportFragment = new ReportFragment();
        userFragment = new UserFragment();

        // Mặc định hiển thị fragment "Thêm giao dịch"
        int selectedFragmentId = pref.getInt("selectedFragment", R.id.trade);
        loadFragment(getFragmentById(selectedFragmentId));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                saveSelectedFragment(itemId);
                loadFragment(getFragmentById(itemId));
                return true;
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel("default", "Kênh chung", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.createNotificationChannel(channel);
            }
        }

    }

    private void loadFragment(Fragment fragment) {
        // Chuyển đổi fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Frame_layout, fragment);
        fragmentTransaction.commit();

        if (fragment instanceof UserFragment) {
            ((UserFragment) fragment).setMainActivity(this);
        }
    }

    private Fragment getFragmentById(int itemId) {
        if (itemId == R.id.add) {
            return addFragment;
        } else if (itemId == R.id.trade) {
            return tradeFragment;
        } else if (itemId == R.id.user) {
            return userFragment;
        } else if (itemId == R.id.report) {
            return reportFragment;
        } else {
            return null;
        }
    }

    private void saveSelectedFragment(int itemId) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("selectedFragment", itemId);
        editor.apply();
    }
}