package com.example.meditracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bind buttons
        Button btnPrivacyPolicy = findViewById(R.id.btn_privacy_policy);
        Button btnNotifications = findViewById(R.id.btn_notifications);
        Button btnAccount = findViewById(R.id.btn_account);
        //Button btnPrivacyPolicyu = findViewById(R.id.btnPrivacyPolicy);

        // Privacy Policy button
        btnPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        // Notification Settings button (placeholder)
        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, NotificationSettingsActivity.class);
            startActivity(intent);
        });

        // Account Settings button (placeholder)
        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });




        // Optional: Add Edit Profile navigation
        // Add this button to activity_settings.xml if desired:
        // <Button android:id="@+id/btn_edit_profile" android:text="Edit Profile" ... />
        /*
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(intent);
            });
        }
        */
    }
}