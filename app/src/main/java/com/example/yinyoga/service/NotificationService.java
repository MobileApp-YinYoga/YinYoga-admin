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

    public void insertNotification(Notification notification) {
        notificationRepository.inseartNotification(notification);
    }

    public void clearAllNotifications() {
        notificationRepository.clearAllNotifications();
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
