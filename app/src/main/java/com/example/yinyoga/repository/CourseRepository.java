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

    // Thêm khóa học mới
    public void insertCourse(String courseName, String dayOfWeek, String time, int capacity, int duration, double price, String classType) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO Courses (CourseName, DayOfWeek, Time, Capacity, Duration, Price, CourseType) VALUES (?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, courseName);
        statement.bindString(2, dayOfWeek);
        statement.bindString(3, time);
        statement.bindLong(4, capacity);
        statement.bindLong(5, duration);
        statement.bindDouble(6, price);
        statement.bindString(7, classType);
        statement.executeInsert();
    }

    // Lấy khóa học theo ID
    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.database.getReadableDatabase();  // Mở cơ sở dữ liệu để đọc
        Course course = null;  // Đối tượng Course sẽ trả về

        // Truy vấn SELECT để lấy khóa học theo CourseId
        String query = "SELECT * FROM Courses WHERE CourseId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});  // Truyền CourseId vào query

        // Kiểm tra nếu cursor có dữ liệu
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("Database", "CourseId: " + cursor.getInt(0) + ", CourseName: " + cursor.getString(1));

            // Lấy các giá trị từ Cursor theo thứ tự cột
            int id = cursor.getInt(0);  // CourseId
            String courseName = cursor.getString(1);  // CourseName
            String dayOfWeek = cursor.getString(2);  // DayOfWeek
            String time = cursor.getString(3);  // Time
            int capacity = cursor.getInt(4);  // Capacity
            int duration = cursor.getInt(5);  // Duration
            double price = cursor.getDouble(6);  // Price
            String courseType = cursor.getString(7);  // CourseType

            // Tạo đối tượng Course từ dữ liệu trong Cursor
            course = new Course(id, courseName, dayOfWeek, time, capacity, duration, price, courseType);
        }

        // Đóng cursor sau khi sử dụng
        if (cursor != null) {
            cursor.close();
        }

        // Trả về đối tượng Course hoặc null nếu không tìm thấy
        return course;
    }

    // Lấy tất cả khóa học
    public Cursor getAllCourses() {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Courses", null);
    }

    // Cập nhật khóa học
    public void updateCourse(int courseId, String courseName, String dayOfWeek, String time, int capacity, int duration, double price, String courseType) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE Courses SET CourseName = ?, DayOfWeek = ?, Time = ?, Capacity = ?, Duration = ?, Price = ?, CourseType = ? WHERE CourseId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, courseName);
        statement.bindString(2, dayOfWeek);
        statement.bindString(3, time);
        statement.bindLong(4, capacity);
        statement.bindLong(5, duration);
        statement.bindDouble(6, price);
        statement.bindString(7, courseType);
        statement.bindLong(8, courseId);
        statement.executeUpdateDelete();
    }

    // Xóa khóa học
    public void deleteCourse(int courseId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM Courses WHERE CourseId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindLong(1, courseId);
        statement.executeUpdateDelete();
    }
}
