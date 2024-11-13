package com.example.yinyoga.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.repository.ClassInstanceRepository;
import com.example.yinyoga.repository.CourseRepository;
import com.example.yinyoga.utils.ImageHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class SyncClassInstanceManager {

    private final FirebaseFirestore db;
    private final Database dbHelper;
    private final ClassInstanceRepository classInstanceRepository;

    public SyncClassInstanceManager(Context context) {
        db = FirebaseFirestore.getInstance();
        dbHelper = new Database(context);
        this.classInstanceRepository = new ClassInstanceRepository(context);
    }

    public void syncClassInstanceToFirestore() {
        // Query your SQLite database to get the data
        Cursor cursor = classInstanceRepository.getAllClassInstances();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String instanceId = cursor.getString(0);
                int courseId = cursor.getInt(1);
                String date = cursor.getString(2);
                String teacher = cursor.getString(3);
                byte[] imageUrl = cursor.getBlob(4);

                byte[] compressedImage = ImageHelper.compressImage(imageUrl);
                // Prepare the data for Firestore
                Map<String, Object> classInstanceMap = new HashMap<>();
                classInstanceMap.put("instanceId", instanceId);
                classInstanceMap.put("courseId", courseId);
                classInstanceMap.put("date", date);
                classInstanceMap.put("teacher", teacher);
                classInstanceMap.put("imageUrl", Base64.encodeToString(compressedImage, Base64.DEFAULT));

                // Push to Firestore
                db.collection("classInstances").document(instanceId)
                        .set(classInstanceMap)
                        .addOnSuccessListener(aVoid -> Log.d("SyncClassInstanceManager", "Class instance synced to Firestore: " + instanceId))
                        .addOnFailureListener(e -> Log.w("SyncClassInstanceManager", "Error syncing data", e));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void syncClassInstanceFromFirestore() {
        db.collection("classInstances")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String instanceId = document.getId();
                            int courseId = document.getLong("courseId").intValue();
                            String date = document.getString("date");
                            String teacher = document.getString("teacher");
                            String base64String = document.getString("imageUrl");

                            // Insert or update the data in SQLite
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("instanceId", instanceId);
                            values.put("courseId", courseId);
                            values.put("date", date);
                            values.put("teacher", teacher);
                            values.put("imageUrl", Base64.decode(base64String, Base64.DEFAULT));

                            // Check if the record already exists to decide insert/update
                            int rowsAffected = db.update("classInstances", values, "instanceId = ?", new String[]{instanceId});
                            if (rowsAffected == 0) {
                                db.insert("classInstances", null, values);
                            }

                            Log.d("SyncClassInstanceManager", "Class instance synced from Firestore: " + courseId);
                        }
                    } else {
                        Log.w("SyncClassInstanceManager", "Error getting Firestore class instance.", task.getException());
                    }
                });
    }
}
