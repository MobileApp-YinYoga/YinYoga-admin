package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Role;

public class RoleRepository {
    private final Database database;

    public RoleRepository(Context context) {
        this.database = new Database(context);
    }

    public Cursor getAllRoles() {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT * FROM roles", null);
    }

    public void insertRole(String roleName, String des) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO roles (roleName, description) VALUES (?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, roleName);
        statement.bindString(2, des);
        statement.executeInsert();
    }

    public Role getRoleById(int roleId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Role role = null;

        String query = "SELECT roleId, roleName, description FROM roles WHERE roleId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(roleId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                String roleName = cursor.getString(1);
                String description = cursor.getString(1);

                role = new Role(id, roleName, description);
            }
            cursor.close();
        }

        return role;
    }

    public Role getRoleByName(String roleName) {
        SQLiteDatabase db = database.getReadableDatabase();
        Role role = null;

        String query = "SELECT roleId, roleName, description FROM roles WHERE roleName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{roleName});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                String description = cursor.getString(2);

                role = new Role(id, roleName, description);
            }
            cursor.close();
        }

        return role;
    }
}
