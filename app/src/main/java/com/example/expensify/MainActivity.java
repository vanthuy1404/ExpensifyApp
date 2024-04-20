package com.example.expensify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LocaleManager localeManager;
    private Fragment addFragment, tradeFragment, reportFragment, userFragment;
    private SavedConfiguration<Integer> savedNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localeManager = new LocaleManager(this);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        savedNavigation = new SavedConfiguration<>("nav", "current_section");

        if (!savedNavigation.getValue().isPresent()) {
            savedNavigation.save(R.id.trade);
        }

        addFragment = new AddFragment();
        tradeFragment = new TradeFragment();
        reportFragment = new ReportFragment();
        userFragment = new UserFragment();

        // Mặc định hiển thị fragment "Thêm giao dịch"

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                savedNavigation.save(item.getItemId());
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

        bottomNavigationView.setSelectedItemId(savedNavigation.getValue().get());

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

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Frame_layout, fragment);
        fragmentTransaction.commit();
    }
}