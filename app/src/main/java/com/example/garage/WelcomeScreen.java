package com.example.garage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;

    Button signupBtn, loginBtn, debugBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);

        auth = FirebaseAuth.getInstance();


        signupBtn = findViewById(R.id.signupBtn);
        loginBtn = findViewById(R.id.loginBtn);

        debugBtn = findViewById(R.id.debugBtn);// Debug only

        signupBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        debugBtn.setOnClickListener(this); // Debug only
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == signupBtn) {
            startActivity(new Intent(getApplicationContext(), signup.class));
        } else if (view == loginBtn) {
            startActivity(new Intent(getApplicationContext(), login.class));
        }

        if (view == debugBtn) { // Debug only
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}