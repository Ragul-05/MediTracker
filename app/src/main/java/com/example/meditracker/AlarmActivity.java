package com.example.meditracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {
    private Button btnStopAlarm;
    private TextView tvMedicineInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tvMedicineInfo = findViewById(R.id.tv_medicine_info);
        btnStopAlarm = findViewById(R.id.btn_stop_alarm);

        // Get medicine details from intent
        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineTime = getIntent().getStringExtra("MEDICINE_TIME");

        if (medicineName != null && medicineTime != null) {
            tvMedicineInfo.setText("Time to take " + medicineName + " at " + medicineTime);
        } else {
            tvMedicineInfo.setText("Time to take your medicine!");
        }

        btnStopAlarm.setOnClickListener(v -> {
            AlarmReceiver.stopAlarm();
            finish();
        });
    }
}
