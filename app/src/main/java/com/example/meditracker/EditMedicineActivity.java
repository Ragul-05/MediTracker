package com.example.meditracker;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Locale;

public class EditMedicineActivity extends AppCompatActivity {
    private static final String TAG = "EditMedicineActivity";
    private EditText etMedicineName, etDosage, etQuantity;
    private Spinner spFrequency;
    private Button btnTime, btnStartDate, btnEndDate, btnSave, btnDelete;
    private String medicineId, selectedTime = "00:00", startDate, endDate;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        medicineId = getIntent().getStringExtra("MEDICINE_ID");

        if (medicineId == null || medicineId.isEmpty()) {
            Toast.makeText(this, "No medicine selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etMedicineName = findViewById(R.id.et_medicine_name);
        etDosage = findViewById(R.id.et_dosage);
        etQuantity = findViewById(R.id.et_quantity);
        spFrequency = findViewById(R.id.sp_frequency);
        btnTime = findViewById(R.id.btn_time);
        btnStartDate = findViewById(R.id.btn_start_date);
        btnEndDate = findViewById(R.id.btn_end_date);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);

        loadMedicineData();

        btnTime.setOnClickListener(v -> selectTime());
        btnSave.setOnClickListener(v -> saveMedicine());
        btnDelete.setOnClickListener(v -> deleteMedicine());
    }

    private void loadMedicineData() {
        db.collection("users").document(userId).collection("medicines").document(medicineId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etMedicineName.setText(documentSnapshot.getString("name"));
                        etDosage.setText(documentSnapshot.getString("dosage"));
                        etQuantity.setText(String.valueOf(documentSnapshot.getLong("quantity")));

                        startDate = documentSnapshot.getString("startDate");
                        btnStartDate.setText("Start: " + (startDate != null ? startDate : "N/A"));
                        endDate = documentSnapshot.getString("endDate");
                        btnEndDate.setText("End: " + (endDate != null ? endDate : "N/A"));

                        String schedule = documentSnapshot.getString("schedule");
                        if (schedule != null) {
                            String[] parts = schedule.split(" ");
                            if (parts.length == 2) {
                                selectedTime = parts[1];
                                btnTime.setText("Time: " + selectedTime);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Medicine not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading medicine", Toast.LENGTH_LONG).show());
    }

    private void selectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view, int hourOfDay, int minute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    btnTime.setText("Time: " + selectedTime);
                },
                12, 0, true
        );
        timePickerDialog.show();
    }

    private void saveMedicine() {
        String name = etMedicineName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String frequency = spFrequency.getSelectedItem().toString();

        if (name.isEmpty() || dosage.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String schedule = frequency + " " + selectedTime;

        db.collection("users").document(userId).collection("medicines").document(medicineId)
                .update("name", name, "dosage", dosage, "quantity", Long.parseLong(quantity),
                        "schedule", schedule, "startDate", startDate, "endDate", endDate, "frequency", frequency)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medicine updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void deleteMedicine() {
        db.collection("users").document(userId).collection("medicines").document(medicineId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medicine deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
