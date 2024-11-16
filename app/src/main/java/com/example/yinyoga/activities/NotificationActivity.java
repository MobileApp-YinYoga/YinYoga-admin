package com.example.yinyoga.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.adapters.NotificationAdapter;
import com.example.yinyoga.models.Notification;
import com.example.yinyoga.service.NotificationService;
import com.example.yinyoga.sync.SyncNotificationManager;
import com.example.yinyoga.utils.DialogHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerNewNotifications, recyclerBeforeNotifications;
    private NotificationAdapter newAdapter, beforeAdapter;
    private List<Notification> newNotifications, beforeNotifications;
    private ImageView menuOptions, backHome;
    private NotificationService notificationService;
    private SyncNotificationManager syncNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_notification), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();

        menuOptions.setOnClickListener(v -> showCustomMenu(menuOptions));
        backHome.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        setupRecyclerView();

        syncNotificationManager = new SyncNotificationManager(this);
        DialogHelper.showLoadingDialog(this, "Loading notifications...");
        loadNotifications();
        DialogHelper.dismissLoadingDialog();
    }

    private void initView() {
        notificationService = new NotificationService(this);
        notificationService.deleteOldNotifications(); // Xóa thông báo cũ hơn 1 năm

        recyclerNewNotifications = findViewById(R.id.recycler_new_notifications);
        recyclerBeforeNotifications = findViewById(R.id.recycler_before_notifications);

        backHome = findViewById(R.id.ic_back);
        menuOptions = findViewById(R.id.menu_options_search);
    }


    private void setupRecyclerView() {
        recyclerNewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerBeforeNotifications.setLayoutManager(new LinearLayoutManager(this));

        newNotifications = new ArrayList<>();
        beforeNotifications = new ArrayList<>();

        newAdapter = new NotificationAdapter(this, newNotifications);
        beforeAdapter = new NotificationAdapter(this, beforeNotifications);

        recyclerNewNotifications.setAdapter(newAdapter);
        recyclerBeforeNotifications.setAdapter(beforeAdapter);
    }

    private void showCustomMenu(View anchor) {
        // Inflate layout for PopupWindow
        View popupView = LayoutInflater.from(this).inflate(R.layout.menu_notification, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Set background to close the Popup when clicking outside
        popupWindow.setOutsideTouchable(true);

        TextView tvMarkAllRead = popupView.findViewById(R.id.tv_mark_all_read);
        TextView tvClearAll = popupView.findViewById(R.id.tv_clear_all);

        tvMarkAllRead.setOnClickListener(v -> {
            markAllNotificationsAsRead();
            popupWindow.dismiss();
        });

        tvClearAll.setOnClickListener(v -> {
            DialogHelper.showConfirmationDialog(
                    this,
                    "Are you sure you want to delete all notifications?",
                    null,
                    null,
                    () -> {
                        clearAllNotifications();
                        loadNotifications();
                        DialogHelper.showSuccessDialog(NotificationActivity.this, "All notifications removed successfully!");
                    });
            popupWindow.dismiss();
        });

        // Show PopupWindow at the position of anchor
        popupWindow.showAsDropDown(anchor, 0, 10);
    }

    private void markAllNotificationsAsRead() {
        notificationService.markAllAsRead();

        for (Notification notification : newNotifications) {
            notification.setRead(true);
        }
        for (Notification notification : beforeNotifications) {
            notification.setRead(true);
        }

        newAdapter.notifyDataSetChanged();
        beforeAdapter.notifyDataSetChanged();

        Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
    }

    private void clearAllNotifications() {
        notificationService.clearAllNotifications();

        newNotifications.clear();
        beforeNotifications.clear();

        newAdapter.notifyDataSetChanged();
        beforeAdapter.notifyDataSetChanged();

        Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
    }

    private void loadNotifications() {
        List<Notification> allNotifications = notificationService.getAllNotifications();
        newNotifications.clear();
        beforeNotifications.clear();

        if (allNotifications.isEmpty()) {
            syncNotificationManager.syncNotificationsFromFirestore();
        } else {
            syncNotificationManager.syncNotificationsToFirestore();
        }

        for (Notification notification : allNotifications) {
            if (notificationService.isToday(notification.getCreatedDate())) {
                newNotifications.add(notification);
            } else {
                beforeNotifications.add(notification);
            }
        }

        newAdapter.notifyDataSetChanged();
        beforeAdapter.notifyDataSetChanged();
    }

    private void addSampleNotifications() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Ngày hôm qua
        String yesterdayDate = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, -3); // Ngày trước đó ba ngày
        String threeDaysAgoDate = dateFormat.format(calendar.getTime());

        notificationService.insertNotification(new Notification("trannq2003@gmail.com", "Today's Notification 1", "Description 1", "09:00 AM", false, todayDate));
        notificationService.insertNotification(new Notification("trannq2003@gmail.com", "Today's Notification 2", "Description 2", "10:00 AM", true, todayDate));
        notificationService.insertNotification(new Notification("trannq2003@gmail.com", "Today's Notification 3", "Description 3", "11:00 AM", false, todayDate));

        notificationService.insertNotification(new Notification("trannq2003@gmail.com", "Old Notification 1", "Description 1", "09:00 AM", true, yesterdayDate));
        notificationService.insertNotification(new Notification("trannq2003@gmail.com", "Old Notification 2", "Description 2", "10:00 AM", true, yesterdayDate));
        notificationService.insertNotification(new Notification("trannq2003@gmail.com", "Old Notification 3", "Description 3", "11:00 AM", false, threeDaysAgoDate));
        notificationService.insertNotification(new Notification("trannq2003@gmail.com", "Old Notification 4", "Description 4", "12:00 PM", false, threeDaysAgoDate));
    }
}
