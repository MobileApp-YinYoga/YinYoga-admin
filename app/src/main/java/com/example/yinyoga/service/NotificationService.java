package com.example.yinyoga.service;

import android.content.Context;

import com.example.yinyoga.models.Notification;
import com.example.yinyoga.repository.NotificationRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(Context context) {
        this.notificationRepository = new NotificationRepository(context);
    }

    // Thêm thông báo mới
    public void insertNotification(Notification notification) {
        notificationRepository.inseartNotification(notification);
    }

    public void clearAllNotifications() {
        notificationRepository.clearAllNotifications();
    }

    public boolean isToday(String createdDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(createdDate);

            // Lấy ngày hiện tại
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return date != null && dateFormat.format(date).equals(dateFormat.format(today.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.getAllNotifications();
    }

    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
    }

    public void deleteOldNotifications() {
        String oneYearAgoDate = getOneYearAgoDate();
        notificationRepository.deleteOldNotifications(oneYearAgoDate);
    }

    private String getOneYearAgoDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
