package com.example.meditracker;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private static final String TAG = "PrivacyPolicy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView webView = findViewById(R.id.webViewPrivacyPolicy);
        if (webView == null) {
            Log.e(TAG, "WebView not found");
            Toast.makeText(this, "Error: Unable to load privacy policy", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript if needed
        webView.loadDataWithBaseURL(null, getPrivacyPolicyHtml(), "text/html", "UTF-8", null);
    }

    private String getPrivacyPolicyHtml() {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: sans-serif; padding: 16px; line-height: 1.6; }" +
                "h2 { color: #2c3e50; }" +
                "h3 { color: #34495e; }" +
                "p { color: #555; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Privacy Policy</h2>" +
                "<p>Welcome to MediTracker! Your privacy is important to us. This Privacy Policy explains how we collect, use, disclose, and safeguard your information.</p>" +

                "<h3>1. Information We Collect</h3>" +
                "<p>We collect personal data such as name, email, age, and phone number during registration. Additionally, we may collect health-related data to improve your experience.</p>" +

                "<h3>2. How We Use Your Information</h3>" +
                "<ul>" +
                "<li>To provide personalized medicine reminders</li>" +
                "<li>To improve user experience and application performance</li>" +
                "<li>To enhance security and prevent unauthorized access</li>" +
                "</ul>" +

                "<h3>3. Data Security</h3>" +
                "<p>We implement robust security measures to protect your data from unauthorized access, alteration, and disclosure.</p>" +

                "<h3>4. Third-Party Services</h3>" +
                "<p>We may integrate with third-party services such as Firebase for authentication and data storage. These services have their own privacy policies.</p>" +

                "<h3>5. Contact Information</h3>" +
                "<p>If you have any questions or concerns about our Privacy Policy, please contact us at:</p>" +
                "<p><strong>Email:</strong> support@meditracker.com</p>" +
                "<p><strong>Phone:</strong> +1-234-567-890</p>" +

                "<p>By using our app, you agree to this Privacy Policy.</p>" +

                "</body>" +
                "</html>";
    }

}
