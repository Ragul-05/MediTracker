package com.example.meditracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlarmReceiver extends BroadcastReceiver {
    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MediTrack:AlarmWakeLock");
        wakeLock.acquire(60 * 1000L /* 1 minute */);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, alarmSound);
            mediaPlayer.setLooping(true);
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 1000, 500, 1000}; // Vibrate for 1s, pause 0.5s, vibrate 1s
            vibrator.vibrate(pattern, -1);
        }

        // Fetch the latest reminder from Firestore
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("reminders")
                    .orderBy("time") // Order by time to get the latest reminder
                    .limit(1) // Get the most recent reminder
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String medicineName = document.getString("medicineName");
                                String time = document.getString("time");

                                Intent alarmIntent = new Intent(context, AlarmActivity.class);
                                alarmIntent.putExtra("MEDICINE_NAME", medicineName);
                                alarmIntent.putExtra("MEDICINE_TIME", time);
                                alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(alarmIntent);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch medicine name", e));
        } else {
            Log.e(TAG, "User is not logged in!");
        }

        // Stop alarm after 1 minute
        new Handler(Looper.getMainLooper()).postDelayed(AlarmReceiver::stopAlarm, 60000);
    }

    public static void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
