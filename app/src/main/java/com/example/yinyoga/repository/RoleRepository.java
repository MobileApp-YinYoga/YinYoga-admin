package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Role;

public class RoleRepository {
    private Database database;

    public RoleRepository(Context context) {
        this.database = new Database(context);
    }

    public Cursor getAllRoles() {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Roles", null);
    }

    public void insertRole(String roleName, String des) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO Roles (RoleName, Description) VALUES (?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, roleName);
        statement.bindString(2, des);
        statement.executeInsert();
    }

    // Phương thức lấy vai trò theo ID
    public Role getRoleById(int roleId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Role role = null;

        // Câu truy vấn SQL để lấy vai trò theo ID
        String query = "SELECT RoleId, RoleName, Description FROM Roles WHERE RoleId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(roleId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Lấy dữ liệu từ các cột
                int id = cursor.getInt(0);
                String roleName = cursor.getString(1);
                String description = cursor.getString(1);

                // Tạo đối tượng Roles
                role = new Role(id, roleName, description);
            }
            cursor.close(); // Đóng cursor sau khi sử dụng
        }

        return role; // Trả về đối tượng Roles (hoặc null nếu không tìm thấy)
    }

    public Role getRoleByName(String roleName) {
        SQLiteDatabase db = database.getReadableDatabase();
        Role role = null;

        // Câu truy vấn SQL để lấy vai trò theo ID
        String query = "SELECT RoleId, RoleName, Description FROM Roles WHERE RoleName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{roleName});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Lấy dữ liệu từ các cột
                int id = cursor.getInt(0);
                String description = cursor.getString(2);

                // Tạo đối tượng Roles
                role = new Role(id, roleName, description);
            }
            cursor.close(); // Đóng cursor sau khi sử dụng
        }

        return role; // Trả về đối tượng Roles (hoặc null nếu không tìm thấy)
    }
}
