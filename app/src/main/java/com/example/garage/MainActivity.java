package com.example.garage;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        activityReplaceFragment(new home());
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        if (item.getItemId() == R.id.homeBtn) {
            fragment = new home();
        }
        if (item.getItemId() == R.id.eventsBtn) {
            fragment = new events();
        }
        if (item.getItemId() == R.id.addBtn) {
            fragment = new add();
        }
        if (item.getItemId() == R.id.userBtn) {
            fragment = new user();
        }
        if (fragment != null) {
            activityReplaceFragment(fragment);
        }
        return true;
    }

    private void activityReplaceFragment(Fragment fragment) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
    }
}