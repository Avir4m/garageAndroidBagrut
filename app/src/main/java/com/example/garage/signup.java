package com.example.garage;

import static com.example.garage.functions.validationUtils.isPasswordValid;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;

    TextView loginTextView;
    Button signUpButton;
    EditText displayNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        auth = FirebaseAuth.getInstance();

        loginTextView = findViewById(R.id.login_text);

        signUpButton = findViewById(R.id.signupBtn);
        signUpButton.setOnClickListener(this);

        displayNameEditText = findViewById(R.id.displayNameInput);

        emailEditText = findViewById(R.id.emailInput);

        passwordEditText = findViewById(R.id.passwordInput);

        confirmPasswordEditText = findViewById(R.id.confirmPasswordInput);


        String text = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(signup.this, login.class));
            }
        };

        spannableString.setSpan(clickableSpan, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginTextView.setText(spannableString);
        loginTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View view) {
        if (view == signUpButton) {
            String displayName, email, password, confirmPassword;
            displayName = displayNameEditText.getText().toString();
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();
            confirmPassword = confirmPasswordEditText.getText().toString();

            if (TextUtils.isEmpty(displayName)) {
                Toast.makeText(signup.this, "Enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(signup.this, "Enter an email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(signup.this, "Enter a password", Toast.LENGTH_SHORT).show();
                return;
            }

            String validationMessage = isPasswordValid(password);

            if (validationMessage != null) {
                Toast.makeText(signup.this, validationMessage, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(signup.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                                    user.updateProfile(profileUpdate)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                        Map<String, Object> userData = new HashMap<>();
                                                        userData.put("name", displayName);
                                                        userData.put("profilePicture", null);

                                                        db.collection("users").document(user.getUid())
                                                                .set(userData)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Toast.makeText(getApplicationContext(), "Account created successfully.", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(getApplicationContext(), "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                });
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}