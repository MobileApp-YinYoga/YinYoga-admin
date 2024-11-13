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
    public void insertClassInstance(String instanceId, int courseId, String date, String teacher, byte[] imageUrl) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO classInstances (instanceId, courseId, date, teacher, imageUrl) VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, instanceId);
        statement.bindLong(2, courseId);
        statement.bindString(3, date);
        statement.bindString(4, teacher);
        statement.bindBlob(5, imageUrl);
        statement.executeInsert();
    }

    // Lấy tất cả các phiên học
    public Cursor getAllClassInstances() {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT classInstances.*, courses.courseName " +
                "FROM classInstances " +
                "JOIN courses ON classInstances.courseId = courses.courseId", null);
    }

    // Cập nhật phiên học
    public void updateClassInstance(String instanceId, int courseId, String date, String teacher, byte[] imageUrl) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE classInstances SET courseId = ?, date = ?, teacher = ?, imageUrl = ? WHERE instanceId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, courseId);
        statement.bindString(2, date);
        statement.bindString(3, teacher);
        statement.bindBlob(4, imageUrl);
        statement.bindString(5, instanceId);
        statement.executeUpdateDelete();
    }

    // Xóa phiên học
    public void deleteClassInstance(String instanceId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM classInstances WHERE instanceId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, instanceId);
        statement.executeUpdateDelete();
    }

    // Lấy phiên học theo ID
    public ClassInstance getClassInstanceById(String instanceId) {
        SQLiteDatabase db = database.getReadableDatabase();
        ClassInstance instance = null;

        String query = "SELECT * FROM classInstances WHERE instanceId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{instanceId});

        try {
            if (cursor != null && cursor.moveToFirst()) {
                // Đảm bảo kiểm tra kỹ vị trí của các cột trong bảng classInstances
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow("courseId"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                byte[] imageUrl = cursor.getBlob(cursor.getColumnIndexOrThrow("imageUrl"));

                // Tạo đối tượng Course
                Course course = new Course();
                course.setCourseId(courseId);

                // Tạo đối tượng ClassInstance
                instance = new ClassInstance(instanceId, course, date, teacher, imageUrl);
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

        Cursor cursor = db.rawQuery("SELECT * FROM classInstances WHERE courseId = ?", new String[]{String.valueOf(courseId)});
        if (cursor.moveToFirst()) {
            do {
                String instanceId =  cursor.getString(cursor.getColumnIndexOrThrow("instanceId"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                byte[] imageUrl = cursor.getBlob(cursor.getColumnIndexOrThrow("imageUrl"));

                // Tạo đối tượng Course
                Course course = new Course();
                course.setCourseId(courseId);

                // Tạo đối tượng ClassInstance
                ClassInstance instance = new ClassInstance(instanceId, course, date, teacher, imageUrl);

                classInstances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classInstances;
    }

}
