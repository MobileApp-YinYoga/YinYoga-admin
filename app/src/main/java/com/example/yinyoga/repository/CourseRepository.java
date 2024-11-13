package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Course;

public class CourseRepository {
    private Database database;

    public CourseRepository(Context context) {
        this.database = new Database(context);
    }

    // Insert new course
    public void insertCourse(String courseName, String courseType, String createdAt,
                             String dayOfWeek, String description, int capacity,
                             int duration, byte[] imageUrl, double price, String time) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO Courses (CourseName, CourseType, CreatedAt, DayOfWeek, Description, Capacity, Duration, ImageUrl, Price, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, courseName);
        statement.bindString(2, courseType);
        statement.bindString(3, createdAt);
        statement.bindString(4, dayOfWeek);
        statement.bindString(5, description);
        statement.bindLong(6, capacity);
        statement.bindLong(7, duration);
        statement.bindBlob(8, imageUrl);
        statement.bindDouble(9, price);
        statement.bindString(10, time);
        statement.executeInsert();
    }

    // Get course by ID
    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.database.getReadableDatabase();
        Course course = null;

        String query = "SELECT * FROM Courses WHERE CourseId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("Database", "CourseId: " + cursor.getInt(0) + ", CourseName: " + cursor.getString(1));

            // Retrieve values from cursor
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

            // Create Course object
            course = new Course(id, courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageUrl, price, time);
        }

        if (cursor != null) {
            cursor.close();
        }

        return course;
    }

    // Get all courses
    public Cursor getAllCourses() {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Courses", null);
    }

    // Update course
    public void updateCourse(int courseId, String courseName, String courseType, String createdAt,
                             String dayOfWeek, String description, int capacity,
                             int duration, byte[] imageUrl, double price, String time) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE Courses SET CourseName = ?, CourseType = ?, CreatedAt = ?, DayOfWeek = ?, Description = ?, Capacity = ?, Duration = ?, ImageUrl = ?, Price = ?, Time = ? WHERE CourseId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, courseName);
        statement.bindString(2, courseType);
        statement.bindString(3, createdAt);
        statement.bindString(4, dayOfWeek);
        statement.bindString(5, description);
        statement.bindLong(6, capacity);
        statement.bindLong(7, duration);
        statement.bindBlob(8, imageUrl);
        statement.bindDouble(9, price);
        statement.bindString(10, time);
        statement.bindLong(11, courseId);
        statement.executeUpdateDelete();
    }

    // Delete course
    public void deleteCourse(int courseId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM Courses WHERE CourseId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindLong(1, courseId);
        statement.executeUpdateDelete();
    }
}
