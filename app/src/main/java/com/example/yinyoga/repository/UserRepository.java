package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Role;
import com.example.yinyoga.models.User;

public class UserRepository {
    private Database database;
    private RoleRepository roleRepository;

    public UserRepository(Context context) {
        this.database = new Database(context);
        this.roleRepository = new RoleRepository(context);
    }

    // Thêm người dùng mới
    public void insertUser(String username, String fullName, String email, String password, int roleId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO users (username, fullName, email, password, roleId) VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, username);
        statement.bindString(2, fullName);
        statement.bindString(3, email);
        statement.bindString(4, password);
        statement.bindLong(5, roleId);
        statement.executeInsert();
    }

    // Lấy người dùng từ email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email}); // Chú ý rằng cần sử dụng chữ "Email" hoa

        if (cursor.moveToFirst()) {
            int roleId = cursor.getInt(4); // Lấy roleId từ cursor
            Role role = getRoleById(roleId); // Gọi hàm lấy Role dựa trên roleId

            User user = new User(
                    cursor.getString(0),  // username
                    cursor.getString(1),  // Full Name
                    cursor.getString(2),  // email
                    cursor.getString(3),  // password
                    role                   // Role được lấy từ database
            );
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Hàm lấy đối tượng Role từ roleId
    private Role getRoleById(int roleId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM roles WHERE roleId = ?", new String[]{String.valueOf(roleId)});
        if (cursor.moveToFirst()) {
            Role role = new Role(
                    cursor.getInt(0),   // roleId
                    cursor.getString(1), // roleName
                    cursor.getString(2) // description
            );
            cursor.close();
            return role;
        }
        cursor.close();
        return null; // Nếu không tìm thấy Role, trả về null
    }

    // Lấy người dùng theo ID
    public User getUserById(String userId) {
        SQLiteDatabase db = database.getReadableDatabase();
        User user = null;

        String query = "SELECT username, fullName, email, password, roleId FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String username = cursor.getString(0);
                String fullName = cursor.getString(1);
                String email = cursor.getString(2);
                String password = cursor.getString(3);
                int roleId = cursor.getInt(4);

                // Lấy role từ roleId, bao gồm roleName
                Role role = roleRepository.getRoleById(roleId);
                if (role != null) {
                    user = new User(username, fullName, email, password, role);
                }else{
                    Log.d("Error while get user by id", "not having role");
                }
            }
            cursor.close();
        }
        return user; // Trả về đối tượng User (hoặc null nếu không tìm thấy)
    }

    // Lấy tất cả người dùng
    public Cursor getAllUsers() {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users", null);
    }

    // Cập nhật thông tin người dùng
    public void updateUser(String username, String fullName, String email, String oldUsername) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE users SET username = ?, fullName = ?, email = ? WHERE username = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, username);
        statement.bindString(2, fullName);
        statement.bindString(3, email);
        statement.bindString(4, oldUsername);
        statement.executeUpdateDelete();
    }

    // Cập nhật mật khẩu
    public void updatePassword(String username, String newPassword) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE users SET password = ? WHERE username = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, newPassword);
        statement.bindString(2, username);
        statement.executeUpdateDelete();
    }

    // Xóa người dùng
    public void deleteUser(String username) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM users WHERE username = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, username);
        statement.executeUpdateDelete();
    }
}
