package com.example.meditracker;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private static final String TAG = "PrivacyPolicy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView webView = findViewById(R.id.webViewPrivacyPolicy);
        if (webView == null) {
            Log.e(TAG, "WebView not found in layout");
            Toast.makeText(this, "Error loading privacy policy", Toast.LENGTH_SHORT).show();
            return;
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String privacyPolicyHtml = "<html><body style='padding:16px; font-size:14px;'>" +
                "<h2>Privacy Policy</h2>" +
                "<p>Welcome to <b>MediTracker</b>. Your privacy is important to us. This policy explains how we collect, use, and protect your data.</p>" +
                "<h3>1. Information We Collect</h3>" +
                "<ul>" +
                "<li>Personal details (name, phone, email, etc.)</li>" +
                "<li>Medication and reminder data</li>" +
                "<li>App usage data and device information</li>" +
                "</ul>" +
                "<h3>2. How We Use Your Information</h3>" +
                "<p>We use your data to manage medicine reminders, improve features, and ensure security. We do not sell your data.</p>" +
                "<h3>3. Security Measures</h3>" +
                "<p>Your data is encrypted and stored securely using Firebase technology.</p>" +
                "<h3>4. Your Rights</h3>" +
                "<p>You can access, update, or delete your data at any time in the app settings.</p>" +
                "<h3>5. Contact Us</h3>" +
                "<p>For any privacy concerns, email us at <b>support@meditracker.com</b>.</p>" +
                "</body></html>";

        webView.loadData(privacyPolicyHtml, "text/html", "UTF-8");
    }
}