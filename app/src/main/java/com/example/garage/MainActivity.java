package com.example.garage;

import static com.example.garage.functions.fragmentUtils.activityReplaceFragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ImageButton settingsBtn, chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        settingsBtn = findViewById(R.id.settingsBtn);
        chatBtn = findViewById(R.id.chatBtn);

        activityReplaceFragment(this, new home(), R.id.frame);
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
            settingsBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.VISIBLE);
        }
        if (item.getItemId() == R.id.eventsBtn) {
            fragment = new events();
            settingsBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
        }
        if (item.getItemId() == R.id.addBtn) {
            fragment = new add();
            settingsBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
        }
        if (item.getItemId() == R.id.userBtn) {
            fragment = new user();
            settingsBtn.setVisibility(View.VISIBLE);
            chatBtn.setVisibility(View.GONE);
        }
        if (fragment != null) {
            activityReplaceFragment(this, fragment, R.id.frame);
        }
        return true;
    }
}