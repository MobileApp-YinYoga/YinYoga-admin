package com.example.yinyoga.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.repository.CourseRepository;
import com.example.yinyoga.utils.ImageHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
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
        List<Course> courseList = courseRepository.getAllCourses();

        for (Course course : courseList) {
            byte[] compressedImage = ImageHelper.compressImage(course.getImageUrl());
            // Prepare the data for Firestore
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("courseName", course.getCourseName());
            courseMap.put("courseType", course.getCourseType());
            courseMap.put("createdAt", course.getCreatedAt());
            courseMap.put("dayOfWeek", course.getDayOfWeek());
            courseMap.put("description", course.getDescription());
            courseMap.put("capacity", course.getCapacity());
            courseMap.put("duration", course.getDuration());
            courseMap.put("imageUrl", Base64.encodeToString(compressedImage, Base64.DEFAULT));
            courseMap.put("price", course.getPrice());
            courseMap.put("time", course.getTime());

            // Push to Firestore
            db.collection("courses").document(String.valueOf(course.getCourseId()))
                    .set(courseMap)
                    .addOnSuccessListener(aVoid -> Log.d("SyncCourseManager", "Course synced to Firestore: " + course.getCourseId()))
                    .addOnFailureListener(e -> Log.w("SyncCourseManager", "Error syncing course", e));
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
