// PATH: app/src/main/java/com/zeroaccess/MainActivity.java
package com.zeroaccess;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private int curId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (savedInstanceState == null) load(new HomeFragment(), false);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == curId) return true;
            Fragment f;
            if      (id == R.id.nav_home)     f = new HomeFragment();
            else if (id == R.id.nav_apps)     f = new AppsFragment();
            else if (id == R.id.nav_ram)      f = new RamMonitorFragment();
            else if (id == R.id.nav_insights) f = new InsightsFragment();
            else                               f = new SettingsFragment();
            curId = id;
            return load(f, true);
        });
    }

    private boolean load(Fragment f, boolean anim) {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (anim) ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.fragment_container, f).commit();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}