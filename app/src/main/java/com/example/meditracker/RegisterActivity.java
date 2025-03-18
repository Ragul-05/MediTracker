package com.example.meditracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etAge, etPhoneNumber;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private boolean isPasswordVisible = false, isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e("FirebaseInitError", "Error initializing Firebase", e);
        }

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etAge = findViewById(R.id.et_age);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        ivToggleConfirmPassword = findViewById(R.id.iv_toggle_confirm_password);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLogin = findViewById(R.id.tv_login);

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility(etPassword, ivTogglePassword, true));
        ivToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, ivToggleConfirmPassword, false));

        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || ageStr.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 150) {
                Toast.makeText(this, "Please enter a valid age (0-150)", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Age must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                        if (!userId.isEmpty()) {
                            saveUserToFirestore(userId, fullName, email, age, phoneNumber);
                        } else {
                            Toast.makeText(this, "Error fetching user ID", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirebaseAuthError", "Registration failed", task.getException());
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String fullName, String email, int age, String phoneNumber) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("fullName", fullName);
        userData.put("email", email);
        userData.put("age", age);
        userData.put("phoneNumber", phoneNumber);

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to save user data", e);
                    Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon, boolean isPasswordField) {
        boolean isVisible = isPasswordField ? isPasswordVisible : isConfirmPasswordVisible;
        isVisible = !isVisible;
        if (isPasswordField) {
            isPasswordVisible = isVisible;
        } else {
            isConfirmPasswordVisible = isVisible;
        }
        editText.setInputType(isVisible ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        toggleIcon.setImageResource(isVisible ? R.drawable.ic_visibility : R.drawable.ic_visibility_off);
        editText.setSelection(editText.getText().length());
    }
}
