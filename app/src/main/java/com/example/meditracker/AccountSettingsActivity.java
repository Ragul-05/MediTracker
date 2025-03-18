package com.example.meditracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountSettingsActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPhoneNumber, etDateOfBirth, etAge, etCurrentPassword, etNewPassword;
    private Button btnSaveChanges, btnChangePassword, btnDeleteAccount, btnLogout;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();
        userId = currentUser != null ? currentUser.getUid() : null;

        // Bind UI elements
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etDateOfBirth = findViewById(R.id.et_date_of_birth);
        etAge = findViewById(R.id.et_age);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);
        btnLogout = findViewById(R.id.btn_logout);

        // Load user data from Firestore
        loadUserData();

        // Set click listeners
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
        btnLogout.setOnClickListener(v -> logout());
    }

    // Load current user data from Firestore
    private void loadUserData() {
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)); // Redirect to Login if not authenticated
            finish();
            return;
        }

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DocumentSnapshot userDoc = documentSnapshot;
                        etFullName.setText(userDoc.getString("fullName"));
                        etEmail.setText(userDoc.getString("email"));
                        etPhoneNumber.setText(userDoc.getString("phoneNumber"));
                        etDateOfBirth.setText(userDoc.getString("dateOfBirth"));
                        Long age = userDoc.getLong("age");
                        if (age != null) {
                            etAge.setText(String.valueOf(age));
                        }
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Save profile changes to Firestore
    private void saveProfileChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        // Validation checks
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(dateOfBirth) || TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
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

        // Prepare updated user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", fullName);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("dateOfBirth", dateOfBirth);
        userData.put("age", age);

        // Update email in Firebase Authentication if it has changed
        if (!email.equals(currentUser.getEmail())) {
            currentUser.updateEmail(email)
                    .addOnSuccessListener(aVoid -> updateFirestore(userData))
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            updateFirestore(userData);
        }
    }

    // Helper method to update Firestore
    private void updateFirestore(Map<String, Object> userData) {
        db.collection("users").document(userId)
                .update(userData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Change password
    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter both current and new passwords", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user before changing password
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
        currentUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Update password in Firebase Authentication
                    currentUser.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                etCurrentPassword.setText("");
                                etNewPassword.setText("");
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show());
    }

    // Confirm account deletion
    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure? This action cannot be undone!")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                .setNegativeButton("No", null)
                .show();
    }

    // Delete account
    private void deleteAccount() {
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete user data from Firestore
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete user from Firebase Authentication
                    currentUser.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Logout
    private void logout() {
        auth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}