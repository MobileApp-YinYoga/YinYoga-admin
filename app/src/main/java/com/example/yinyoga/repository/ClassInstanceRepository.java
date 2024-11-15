package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.models.Course;

import java.util.ArrayList;
import java.util.List;

public class ClassInstanceRepository {
    private final Database database;

    public ClassInstanceRepository(Context context) {
        this.database = new Database(context);
    }

    public void insertClassInstance(ClassInstance classInstance) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO classInstances (instanceId, courseId, date, teacher, imageUrl) VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, classInstance.getInstanceId());
        statement.bindLong(2, classInstance.getCourse().getCourseId());
        statement.bindString(3, classInstance.getDate());
        statement.bindString(4, classInstance.getTeacher());
        statement.bindBlob(5, classInstance.getImageUrl());
        statement.executeInsert();
    }

    public List<ClassInstance> getAllClassInstances() {
        List<ClassInstance> instanceLists = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT classInstances.*, courses.courseName " +
                "FROM classInstances " +
                "JOIN courses ON classInstances.courseId = courses.courseId", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String instanceId = cursor.getString(0);
                int courseId = cursor.getInt(1);
                String date = cursor.getString(2);
                String teacher = cursor.getString(3);
                byte[] imageUrl = cursor.getBlob(4);
                String courseName = cursor.getString(5);

                Course course = new Course();
                course.setCourseId(courseId);
                course.setCourseName(courseName);

                instanceLists.add(new ClassInstance(instanceId, course, date, teacher, imageUrl));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return instanceLists;
    }

    public void updateClassInstance(ClassInstance classInstance) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE classInstances SET courseId = ?, date = ?, teacher = ?, imageUrl = ? WHERE instanceId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, classInstance.getCourse().getCourseId());
        statement.bindString(2, classInstance.getDate());
        statement.bindString(3, classInstance.getTeacher());
        statement.bindBlob(4, classInstance.getImageUrl());
        statement.bindString(5, classInstance.getInstanceId());
        statement.executeUpdateDelete();
    }

    public void deleteClassInstance(String instanceId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM classInstances WHERE instanceId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, instanceId);
        statement.executeUpdateDelete();
    }

    public ClassInstance getClassInstanceById(String instanceId) {
        SQLiteDatabase db = database.getReadableDatabase();
        ClassInstance instance = null;

        String query = "SELECT * FROM classInstances WHERE instanceId = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{instanceId})) {
            if (cursor != null && cursor.moveToFirst()) {
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow("courseId"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                byte[] imageUrl = cursor.getBlob(cursor.getColumnIndexOrThrow("imageUrl"));

                Course course = new Course();
                course.setCourseId(courseId);

                instance = new ClassInstance(instanceId, course, date, teacher, imageUrl);
            }
        } catch (Exception e) {
            Log.e("getClassInstanceById", "Error fetching ClassInstance by ID", e);
        }

        return instance;
    }

    public List<ClassInstance> getClassInstancesByCourseId(int courseId) {
        List<ClassInstance> classInstances = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM classInstances WHERE courseId = ?", new String[]{String.valueOf(courseId)});
        if (cursor.moveToFirst()) {
            do {
                String instanceId = cursor.getString(cursor.getColumnIndexOrThrow("instanceId"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                byte[] imageUrl = cursor.getBlob(cursor.getColumnIndexOrThrow("imageUrl"));

                Course course = new Course();
                course.setCourseId(courseId);

                ClassInstance instance = new ClassInstance(instanceId, course, date, teacher, imageUrl);
                classInstances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classInstances;
    }
}
