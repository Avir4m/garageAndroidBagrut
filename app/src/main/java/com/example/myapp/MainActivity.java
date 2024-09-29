package com.example.myapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapp.addCustomer.add_cust;
import com.example.myapp.allCustomer.all_cust;
import com.example.myapp.editCustomer.edit_cust;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        loadFragment(new add_cust());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.relative, fragment).commit();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        if (item.getItemId() == R.id.mnuUserAdd) {
            fragment = new add_cust();
        }
        if (item.getItemId() == R.id.mnuUserEdit) {
            fragment = new edit_cust();
        }
        if (item.getItemId() == R.id.mnuUserGroup) {
            fragment = new all_cust();
        }
        if (fragment != null) {
            loadFragment(fragment);
        }
        return true;
    }
}