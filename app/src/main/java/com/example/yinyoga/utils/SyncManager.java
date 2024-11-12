package com.example.yinyoga.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.repository.CourseRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class SyncManager {

    private FirebaseFirestore db;
    private Database dbHelper;
    private CourseRepository courseRepository;

    public SyncManager(Context context) {
        db = FirebaseFirestore.getInstance();
        dbHelper = new Database(context);
        this.courseRepository = new CourseRepository(context);
    }

    // Sync data from SQLite to Firestore
    public void syncDataToFirestore() {
        // Query your SQLite database to get the data
        Cursor cursor = courseRepository.getAllCourses();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int courseId = cursor.getInt(0);
                String courseName = cursor.getString(1);
                String courseType = cursor.getString(2);
                String createdAt = cursor.getString(3);
                String dayOfWeek = cursor.getString(4);
                String description = cursor.getString(5);
                int capacity = cursor.getInt(6);
                int duration = cursor.getInt(7);
                byte[] imageUrl = cursor.getBlob(8);
                double price = cursor.getDouble(9);
                String time = cursor.getString(10);

                // Prepare the data for Firestore
                Map<String, Object> userMap = new HashMap<>();
//                userMap.put("name", name);
//                userMap.put("email", email);
                userMap.put("courseName", courseName);
                userMap.put("courseType", courseType);
                userMap.put("createdAt", createdAt);
                userMap.put("dayOfWeek", dayOfWeek);
                userMap.put("description", description);
                userMap.put("capacity", capacity);
                userMap.put("duration", duration);
                userMap.put("imageUrl", Base64.encodeToString(imageUrl, Base64.DEFAULT));
                userMap.put("price", price);
                userMap.put("time", time);

                // Push to Firestore
                db.collection("Courses").document(String.valueOf(courseId))
                        .set(userMap)
                        .addOnSuccessListener(aVoid -> Log.d("SyncManager", "Data synced to Firestore for course: " + courseId))
                        .addOnFailureListener(e -> Log.w("SyncManager", "Error syncing data", e));

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void syncDataFromFirestore() {
        db.collection("Courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String courseId = document.getId();
                            String courseName = document.getString("courseName");
                            String courseType = document.getString("courseType");
                            String createdAt = document.getString("createdAt");
                            String dayOfWeek = document.getString("dayOfWeek");
                            String description = document.getString("description");
                            int capacity = document.getLong("capacity").intValue();
                            int duration = document.getLong("duration").intValue();
                            String base64String = document.getString("imageUrl");
                            double price = document.getDouble("price").doubleValue();
                            String time = document.getString("time");

                            // Insert or update the data in SQLite
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("CourseId", courseId);
                            values.put("CourseName", courseName);
                            values.put("CourseType", courseType);
                            values.put("CreatedAt", createdAt);
                            values.put("DayOfWeek", dayOfWeek);
                            values.put("Description", description);
                            values.put("Capacity", capacity);
                            values.put("Duration", duration);
                            values.put("ImageUrl", Base64.decode(base64String, Base64.DEFAULT));
                            values.put("Price", price);
                            values.put("Time", time);

                            // Check if the record already exists to decide insert/update
                            int rowsAffected = db.update("Courses", values, "CourseId = ?", new String[]{courseId});
                            if (rowsAffected == 0) {
                                db.insert("Courses", null, values);
                            }

                            Log.d("SyncManager", "Data synced from Firestore for course: " + courseId);
                        }
                    } else {
                        Log.w("SyncManager", "Error getting Firestore data.", task.getException());
                    }
                });
    }
}
