package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.models.Role;
import com.example.yinyoga.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final Database database;
    private final RoleRepository roleRepository;

    public UserRepository(Context context) {
        this.database = new Database(context);
        this.roleRepository = new RoleRepository(context);
    }

    public void resetDatabase() {
        database.resetDatabase();
    }

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

    public User getUserByEmail(String email) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            int roleId = cursor.getInt(4);
            Role role = getRoleById(roleId);

            User user = new User(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    role
            );
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    private Role getRoleById(int roleId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM roles WHERE roleId = ?", new String[]{String.valueOf(roleId)});
        if (cursor.moveToFirst()) {
            Role role = new Role(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = database.getReadableDatabase();
        User user = null;

        String query = "SELECT username, fullName, email, password, roleId FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});

        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(0);
            String fullName = cursor.getString(1);
            String email = cursor.getString(2);
            String password = cursor.getString(3);
            int roleId = cursor.getInt(4);

            Role role = roleRepository.getRoleById(roleId);
            if (role != null) {
                user = new User(username, fullName, email, password, role);
            } else {
                Log.d("Error while get user by id", "not having role");
            }
        }

        cursor.close();
        return user;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                String fullName = cursor.getString(1);
                String email = cursor.getString(2);
                String password = cursor.getString(3);
                int roleId = cursor.getInt(4);

                Role role = roleRepository.getRoleById(roleId);
                if (role != null) {
                    userList.add(new User(username, fullName, email, password, role));
                } else {
                    Log.d("Error while get user by id", "not having role");
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userList;
    }

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

    public void updatePassword(String username, String newPassword) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE users SET password = ? WHERE username = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, newPassword);
        statement.bindString(2, username);
        statement.executeUpdateDelete();
    }

    public void deleteUser(String username) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "DELETE FROM users WHERE username = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, username);
        statement.executeUpdateDelete();
    }
}
