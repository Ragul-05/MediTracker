package com.example.meditracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class NotificationSettingsActivity extends AppCompatActivity {
    private static final String TAG = "NotificationSettings";
    private TimePicker timePicker;
    private Button btnSetReminder;
    private boolean medicineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        timePicker = findViewById(R.id.time_picker);
        btnSetReminder = findViewById(R.id.btn_set_reminder);

        btnSetReminder.setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            setAlarm(hour, minute);
        });
    }

    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("MEDICINE_NAME", medicineName);
        intent.putExtra("MEDICINE_TIME", hour + ":" + String.format("%02d", minute));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Save to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("medicineName", medicineName);
        reminderData.put("time", hour + ":" + String.format("%02d", minute));

        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("reminders")
                .add(reminderData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Reminder saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving reminder", e));

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Reminder Set for " + medicineName + " at " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
    }

}