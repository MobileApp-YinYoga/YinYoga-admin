package com.example.yinyoga.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.repository.CourseRepository;
import com.example.yinyoga.utils.ImageHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class SyncCourseManager {

    private FirebaseFirestore db;
    private Database dbHelper;
    private CourseRepository courseRepository;

    public SyncCourseManager(Context context) {
        db = FirebaseFirestore.getInstance();
        dbHelper = new Database(context);
        this.courseRepository = new CourseRepository(context);
    }

    public void syncCoursesToFirestore() {
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

                byte[] compressedImage = ImageHelper.compressImage(imageUrl);
                // Prepare the data for Firestore
                Map<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseName", courseName);
                courseMap.put("courseType", courseType);
                courseMap.put("createdAt", createdAt);
                courseMap.put("dayOfWeek", dayOfWeek);
                courseMap.put("description", description);
                courseMap.put("capacity", capacity);
                courseMap.put("duration", duration);
                courseMap.put("imageUrl", Base64.encodeToString(compressedImage, Base64.DEFAULT));
                courseMap.put("price", price);
                courseMap.put("time", time);

                // Push to Firestore
                db.collection("courses").document(String.valueOf(courseId))
                        .set(courseMap)
                        .addOnSuccessListener(aVoid -> Log.d("SyncCourseManager", "Course synced to Firestore: " + courseId))
                        .addOnFailureListener(e -> Log.w("SyncCourseManager", "Error syncing course", e));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void syncCourseFromFirestore() {
        db.collection("courses")
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
                            values.put("courseId", courseId);
                            values.put("courseName", courseName);
                            values.put("courseType", courseType);
                            values.put("createdAt", createdAt);
                            values.put("dayOfWeek", dayOfWeek);
                            values.put("description", description);
                            values.put("capacity", capacity);
                            values.put("duration", duration);
                            values.put("imageUrl", Base64.decode(base64String, Base64.DEFAULT));
                            values.put("price", price);
                            values.put("time", time);

                            // Check if the record already exists to decide insert/update
                            int rowsAffected = db.update("courses", values, "courseId = ?", new String[]{courseId});
                            if (rowsAffected == 0) {
                                db.insert("courses", null, values);
                            }

                            Log.d("SyncCourseManager", "Course synced from Firestore: " + courseId);
                        }
                    } else {
                        Log.w("SyncCourseManager", "Error getting Firestore course.", task.getException());
                    }
                });
    }
}
