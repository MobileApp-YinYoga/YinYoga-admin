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

    // Phương thức để kiểm tra xem ngày thông báo có phải là ngày hôm nay không
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

    // Lấy tất cả thông báo
    public List<Notification> getAllNotifications() {
        return notificationRepository.getAllNotifications();
    }

    // Đánh dấu tất cả thông báo là đã đọc
    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
    }

    // Xóa thông báo cũ hơn 1 năm
    public void deleteOldNotifications() {
        String oneYearAgoDate = getOneYearAgoDate();
        notificationRepository.deleteOldNotifications(oneYearAgoDate);
    }

    // Lấy ngày cách đây 1 năm dưới định dạng yyyy-MM-dd
    private String getOneYearAgoDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
