package com.example.yinyoga.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private Database database;

    public NotificationRepository(Context context) {
        this.database = new Database(context);
    }

    // Thêm thông báo mới vào cơ sở dữ liệu
    public void inseartNotification(Notification notification) {
        try (SQLiteDatabase db = database.getWritableDatabase()) {
            String query = "INSERT INTO notifications (title, description, time, isRead, createdDate) VALUES (?, ?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(query);
            statement.clearBindings();
            statement.bindString(1, notification.getTitle());
            statement.bindString(2, notification.getDescription());
            statement.bindString(3, notification.getTime());
            statement.bindLong(4, notification.isRead() ? 1 : 0);
            statement.bindString(5, notification.getCreatedDate());
            statement.executeInsert();
        }
    }

    // Lấy tất cả thông báo từ cơ sở dữ liệu
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications ORDER BY createdDate DESC";

        try (SQLiteDatabase db = database.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                Notification notification = new Notification(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) == 1,
                        cursor.getString(5)
                );
                notifications.add(notification);
            }
        }
        return notifications;
    }

    // Đánh dấu tất cả thông báo là đã đọc
    public void markAllAsRead() {
        try (SQLiteDatabase db = database.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("isRead", 1);
            db.update("notifications", values, null, null);
        }
    }

    // Xóa tất cả thông báo cũ hơn 1 năm
    public void deleteOldNotifications(String oneYearAgoDate) {
        try (SQLiteDatabase db = database.getWritableDatabase()) {
            db.delete("notifications", "createdDate < ?", new String[]{oneYearAgoDate});
        }
    }

    // Xóa tất cả thông báo
    public void clearAllNotifications() {
        try (SQLiteDatabase db = database.getWritableDatabase()) {
            db.delete("notifications", null, null);
        }
    }
}
