package com.example.yinyoga.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.repository.ClassInstanceRepository;
import com.example.yinyoga.utils.ImageHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
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

    public void resetClassInstanceInFirestore() {
        // Reset classInstances collection
        db.collection("classInstances")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            db.collection("classInstances").document(docId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("SyncManager", "Document " + docId + " deleted from classInstances"))
                                    .addOnFailureListener(e -> Log.w("SyncManager", "Error deleting document from classInstances", e));
                        }
                        Log.d("SyncManager", "All documents in 'classInstances' collection deleted");
                    } else {
                        Log.w("SyncManager", "Error fetching documents from classInstances for deletion.", task.getException());
                    }
                });
    }

    public void syncClassInstanceToFirestore() {
        // Query your SQLite database to get the data
        List<ClassInstance> classInstanceList = classInstanceRepository.getAllClassInstances();

        for (ClassInstance classInstance : classInstanceList) {
            byte[] compressedImage = ImageHelper.compressImage(classInstance.getImageUrl());
            // Prepare the data for Firestore
            Map<String, Object> classInstanceMap = new HashMap<>();
            classInstanceMap.put("instanceId", classInstance.getInstanceId());
            classInstanceMap.put("courseId", classInstance.getCourse().getCourseId());
            classInstanceMap.put("date", classInstance.getDate());
            classInstanceMap.put("teacher", classInstance.getTeacher());
            classInstanceMap.put("imageUrl", Base64.encodeToString(compressedImage, Base64.DEFAULT));

            // Push to Firestore
            db.collection("classInstances").document(classInstance.getInstanceId())
                    .set(classInstanceMap)
                    .addOnSuccessListener(aVoid -> Log.d("SyncClassInstanceManager", "Class instance synced to Firestore: " + classInstance.getInstanceId()))
                    .addOnFailureListener(e -> Log.w("SyncClassInstanceManager", "Error syncing data", e));
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

                            Log.d("SyncClassInstanceManager", "Class instance synced from Firestore: " + instanceId);
                        }
                    } else {
                        Log.w("SyncClassInstanceManager", "Error getting Firestore class instance.", task.getException());
                    }
                });
    }

    public void deleteClassInstanceOnFirebase(String classInstanceId) {
        // If deletion is successful in SQLite, proceed to delete from Firestore
        db.collection("classInstances").document(classInstanceId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("SyncClassInstanceManager", "Class instance deleted on Firestore: " + classInstanceId))
                .addOnFailureListener(e -> Log.w("SyncClassInstanceManager", "Error deleting Class instance on Firestore", e));
    }
}
