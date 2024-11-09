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
    private Database database;

    public ClassInstanceRepository(Context context) {
        this.database = new Database(context);
    }

    // Thêm phiên học mới
    public void insertClassInstance(String instanceId, int courseId, String date, String teacher) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO ClassInstances (InstanceId, CourseId, Date, Teacher) VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, instanceId);
        statement.bindLong(2, courseId);
        statement.bindString(3, date);
        statement.bindString(4, teacher);
        statement.executeInsert();
    }

    // Lấy tất cả các phiên học
    public Cursor getAllClassInstances() {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT ClassInstances.*, Courses.CourseName " +
                "FROM ClassInstances " +
                "JOIN Courses ON ClassInstances.CourseId = Courses.CourseId", null);
    }

    // Cập nhật phiên học
    public void updateClassInstance(String instanceId, int courseId, String date, String teacher) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE ClassInstances SET CourseId = ?, Date = ?, Teacher = ? WHERE InstanceId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, courseId);
        statement.bindString(2, date);
        statement.bindString(3, teacher);
        statement.bindString(4, instanceId);
        statement.executeUpdateDelete();
    }

    // Xóa phiên học
    public void deleteClassInstance(String instanceId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM ClassInstances WHERE InstanceId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, instanceId);
        statement.executeUpdateDelete();
    }

    // Lấy phiên học theo ID
    public ClassInstance getClassInstanceById(String instanceId) {
        SQLiteDatabase db = database.getReadableDatabase();
        ClassInstance instance = null;

        String query = "SELECT * FROM ClassInstances WHERE InstanceId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{instanceId});

        try {
            if (cursor != null && cursor.moveToFirst()) {
                // Đảm bảo kiểm tra kỹ vị trí của các cột trong bảng ClassInstances
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow("CourseId"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("Teacher"));

                // Tạo đối tượng Course
                Course course = new Course();
                course.setCourseId(courseId);

                // Tạo đối tượng ClassInstance
                instance = new ClassInstance(instanceId, course, date, teacher);
            }
        } catch (Exception e) {
            // Ghi lại bất kỳ lỗi nào nếu xảy ra khi xử lý cursor hoặc truy vấn
            Log.e("getClassInstanceById", "Error fetching ClassInstance by ID", e);
        } finally {
            if (cursor != null) {
                cursor.close();  // Đảm bảo đóng Cursor để tránh memory leak
            }
        }

        return instance;
    }

    public List<ClassInstance> getClassInstancesByCourseId(int courseId) {
        List<ClassInstance> classInstances = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM ClassInstances WHERE courseId = ?", new String[]{String.valueOf(courseId)});
        if (cursor.moveToFirst()) {
            do {
                String instanceId =  cursor.getString(cursor.getColumnIndexOrThrow("InstanceId"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("Teacher"));

                // Tạo đối tượng Course
                Course course = new Course();
                course.setCourseId(courseId);

                // Tạo đối tượng ClassInstance
                ClassInstance instance = new ClassInstance(instanceId, course, date, teacher);

                classInstances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classInstances;
    }

}
