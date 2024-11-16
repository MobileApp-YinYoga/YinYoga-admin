package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    private final Database database;

    public CourseRepository(Context context) {
        this.database = new Database(context);
    }

    public void insertCourse(Course course) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO courses (courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageUrl, price, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, course.getCourseName());
        statement.bindString(2, course.getCourseType());
        statement.bindString(3, course.getCreatedAt());
        statement.bindString(4, course.getDayOfWeek());
        statement.bindString(5, course.getDescription());
        statement.bindLong(6, course.getCapacity());
        statement.bindLong(7, course.getDuration());
        statement.bindBlob(8, course.getImageUrl());
        statement.bindDouble(9, course.getPrice());
        statement.bindString(10, course.getTime());
        statement.executeInsert();
    }

    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.database.getReadableDatabase();
        Course course = null;

        String query = "SELECT * FROM courses WHERE courseId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("Database", "courseId: " + cursor.getInt(0) + ", courseName: " + cursor.getString(1));

            int id = cursor.getInt(0);
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

            course = new Course(id, courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageUrl, price, time);
        }

        if (cursor != null) {
            cursor.close();
        }

        return course;
    }

    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM courses", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
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

                courseList.add(new Course(id, courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageUrl, price, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return courseList;
    }

    public void updateCourse(Course course) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE courses SET courseName = ?, courseType = ?, createdAt = ?, dayOfWeek = ?, description = ?, capacity = ?, duration = ?, imageUrl = ?, price = ?, time = ? WHERE courseId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, course.getCourseName());
        statement.bindString(2, course.getCourseType());
        statement.bindString(3, course.getCreatedAt());
        statement.bindString(4, course.getDayOfWeek());
        statement.bindString(5, course.getDescription());
        statement.bindLong(6, course.getCapacity());
        statement.bindLong(7, course.getDuration());
        statement.bindBlob(8, course.getImageUrl());
        statement.bindDouble(9, course.getPrice());
        statement.bindString(10, course.getTime());
        statement.bindLong(11, course.getCourseId());
        statement.executeUpdateDelete();
    }

    public void deleteCourse(int courseId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM courses WHERE courseId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindLong(1, courseId);
        statement.executeUpdateDelete();
    }
}
