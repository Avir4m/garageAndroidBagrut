package com.example.garage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeScreen extends AppCompatActivity {

    FirebaseAuth auth;
    Button signupBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);

        auth = FirebaseAuth.getInstance();

        signupBtn = findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), signup.class)));

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), login.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}