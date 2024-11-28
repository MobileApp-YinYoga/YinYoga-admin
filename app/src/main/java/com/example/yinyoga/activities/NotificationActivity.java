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

import androidx.activity.EdgeToEdge;
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
import com.example.yinyoga.utils.DatetimeHelper;
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
    private ImageView menuOptions, backHome, im_empty;
    private NotificationService notificationService;
    private TextView tv_before_title, tv_new_title;
    private SyncNotificationManager syncNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        EdgeToEdge.enable(this);
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
        loadNotifications();
    }

    private void initView() {
        notificationService = new NotificationService(this);
        notificationService.deleteOldNotifications(); // Delete notifications older than 1 year

        recyclerNewNotifications = findViewById(R.id.recycler_new_notifications);
        recyclerBeforeNotifications = findViewById(R.id.recycler_before_notifications);

        backHome = findViewById(R.id.ic_back);
        menuOptions = findViewById(R.id.menu_options_search);
        im_empty = findViewById(R.id.im_empty);
        tv_new_title = findViewById(R.id.tv_new_title);
        tv_before_title = findViewById(R.id.tv_before_title);

        syncNotificationManager = new SyncNotificationManager(this);
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

        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set background to close the Popup when clicking outside
        popupWindow.setOutsideTouchable(true);

        TextView tvMarkAllRead = popupView.findViewById(R.id.tv_mark_all_read);
        TextView tvClearAll = popupView.findViewById(R.id.tv_clear_all);

        tvMarkAllRead.setOnClickListener(v -> {
            markAllNotificationsAsRead();
            popupWindow.dismiss();
        });

        tvClearAll.setOnClickListener(v -> {
            DialogHelper.showConfirmationDialog(this, "Are you sure you want to delete all notifications?", null, null, () -> {
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
        loadNotifications();

        newNotifications.clear();
        beforeNotifications.clear();

        newAdapter.notifyDataSetChanged();
        beforeAdapter.notifyDataSetChanged();

        Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
    }

    private void loadNotifications() {
        syncNotificationManager.syncNotificationsToFirestore();
        syncNotificationManager.syncNotificationsFromFirestore();
        
        List<Notification> allNotifications = notificationService.getAllNotifications();

        if (allNotifications.isEmpty()) {
            im_empty.setVisibility(View.VISIBLE);
            tv_before_title.setVisibility(View.GONE);
            tv_new_title.setVisibility(View.GONE);
            return;
        }

        im_empty.setVisibility(View.GONE);
        newNotifications.clear();
        beforeNotifications.clear();

        for (Notification notification : allNotifications) {
            if (DatetimeHelper.isToday(notification.getCreatedDate())) {
                newNotifications.add(notification);
            } else {
                beforeNotifications.add(notification);
            }
        }

        newAdapter.notifyDataSetChanged();
        beforeAdapter.notifyDataSetChanged();
    }
}
