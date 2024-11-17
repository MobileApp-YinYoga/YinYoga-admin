package com.example.yinyoga.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Notification;
import com.example.yinyoga.repository.CourseRepository;
import com.example.yinyoga.repository.NotificationRepository;
import com.example.yinyoga.utils.ImageHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncNotificationManager {

    private FirebaseFirestore db;
    private Database dbHelper;
    private final NotificationRepository notificationRepository;

    public SyncNotificationManager(Context context) {
        db = FirebaseFirestore.getInstance();
        dbHelper = new Database(context);
        this.notificationRepository = new NotificationRepository(context);
    }

    public void resetNotificationInFirestore() {
        // Reset courses collection
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            db.collection("notifications").document(docId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("SyncManager", "Document " + docId + " deleted from notifications"))
                                    .addOnFailureListener(e -> Log.w("SyncManager", "Error deleting document from notifications", e));
                        }
                        Log.d("SyncManager", "All documents in 'notifications' collection deleted");
                    } else {
                        Log.w("SyncManager", "Error fetching documents from notifications for deletion.", task.getException());
                    }
                });
    }

    public void syncNotificationsToFirestore() {
        // Query your SQLite database to get the data
        List<Notification> notificationList = notificationRepository.getAllNotifications();

        for (Notification notification : notificationList) {
            // Prepare the data for Firestore
            Map<String, Object> notificationMap = new HashMap<>();

            notificationMap.put("email", notification.getEmail());
            notificationMap.put("title", notification.getTitle());
            notificationMap.put("description", notification.getDescription());
            notificationMap.put("time", notification.getTime());
            notificationMap.put("isRead", notification.isRead());
            notificationMap.put("createdDate", notification.getCreatedDate());

            // Push to Firestore
            db.collection("notifications").document(String.valueOf(notification.getId()))
                    .set(notificationMap)
                    .addOnSuccessListener(aVoid -> Log.d("SyncNotificationManager", "Notification synced to Firestore: " + notification.getId()))
                    .addOnFailureListener(e -> Log.w("SyncNotificationManager", "Error syncing Notification", e));
        }
    }

    public void syncNotificationsFromFirestore() {
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String notificationId = document.getId();
                            String email = document.getString("email");
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String time = document.getString("time");
                            boolean isRead = document.getBoolean("isRead").booleanValue();
                            String createdDate = document.getString("createdDate");

                            // Insert or update the data in SQLite
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("notificationId", notificationId);
                            values.put("email", email);
                            values.put("title", title);
                            values.put("description", description);
                            values.put("time", time);
                            values.put("isRead", isRead);
                            values.put("createdDate", createdDate);

                            // Check if the record already exists to decide insert/update
                            int rowsAffected = db.update("notifications", values, "notificationId = ?", new String[]{notificationId});
                            if (rowsAffected == 0) {
                                db.insert("notifications", null, values);
                            }

                            Log.d("SyncNotificationManager", "Notification synced from Firestore: " + notificationId);
                        }
                    } else {
                        Log.w("SyncNotificationManager", "Error getting Firestore Notification.", task.getException());
                    }
                });
    }
}
