package com.example.yinyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.yinyoga.R;
import com.example.yinyoga.fragments.ManageBookingFragment;
import com.example.yinyoga.fragments.ManageClassInstancesFragment;
import com.example.yinyoga.fragments.ManageCoursesFragment;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.service.CourseService;
import com.example.yinyoga.sync.SyncClassInstanceManager;
import com.example.yinyoga.sync.SyncCourseManager;
import com.example.yinyoga.sync.SyncNotificationManager;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.NetworkUtil;
import com.example.yinyoga.utils.UserSessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkUtil.NetworkListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon, notificationIcon;
    private TextView username, roleName;
    private boolean isNetworkConnected = false;
    private SyncCourseManager syncCourseManager;
    private SyncClassInstanceManager syncClassInstanceManager;
    private SyncNotificationManager syncNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        isNetworkConnected = true;
        NetworkUtil.monitorNetwork(this);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        initView();
        authentication(savedInstanceState);
        setEventNotification();
    }

    private void initView() {
        // Initialize views from the layout
        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menu_icon);
        notificationIcon = findViewById(R.id.notification_icon);
        navigationView = findViewById(R.id.navigation_view);

        // Set up the Drawer Toggle to open and close the navigation bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Access the NavigationView
        navigationView = findViewById(R.id.navigation_view);

        // Get the header view from the NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Access TextViews in nav_header
        username = headerView.findViewById(R.id.user_name);
        roleName = headerView.findViewById(R.id.user_role);

        syncCourseManager = new SyncCourseManager(this);
        syncClassInstanceManager = new SyncClassInstanceManager(this);
        syncNotificationManager = new SyncNotificationManager(this);
    }

    private void authentication(Bundle savedInstanceState) {
        UserSessionManager sessionManager = new UserSessionManager(this);

        // Check if the user is logged in
        if (sessionManager.isLoggedIn()) {
            username.setText(sessionManager.getUsername());
            roleName.setText(sessionManager.getRole());

            setEventMenu(savedInstanceState);
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void setEventMenu(Bundle savedInstanceState) {
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START); // Close the navigation bar
            } else {
                drawerLayout.openDrawer(GravityCompat.START); // Open the navigation bar
            }
        });

        // Set event when selecting an item from the navigation bar
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_manage_classes) {
                selectedFragment = new ManageCoursesFragment();
            } else if (id == R.id.nav_manage_class_instances) {
                selectedFragment = new ManageClassInstancesFragment();
            } else if (id == R.id.nav_manage_booking) {
                selectedFragment = new ManageBookingFragment();
            } else if (id == R.id.nav_profile) {
                // Start the Activity for the user profile page
                Intent intent = new Intent(MainActivity.this, ManageUserActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_notifications) {
                // Start the Activity for the notification page
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                DialogHelper.showConfirmationDialog(
                        MainActivity.this,
                        "Are you sure you want to log out?",
                        null,
                        null,
                        () -> {
                            UserSessionManager sessionManager = new UserSessionManager(MainActivity.this);
                            sessionManager.logout();

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                );
            }

            // Replace the Fragment in the FrameLayout if a Fragment is selected
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }

            drawerLayout.closeDrawer(GravityCompat.START); // Close the navigation bar
            return true;
        });

        // By default, open the ManageCoursesFragment when the app is opened
        if (savedInstanceState == null) {
            replaceFragment(new ManageCoursesFragment());
        }
    }

    private void setEventNotification() {
        notificationIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onAvailable() {
        // Network connected
        if (!isNetworkConnected) {
            Toast.makeText(this, "Wi-Fi Connected", Toast.LENGTH_SHORT).show();
            isNetworkConnected = true;

            DialogHelper.showLoadingDialog(this, "Wait sync data...");

            // Sync courses
            syncCourseManager.syncCoursesToFirestore();
            syncCourseManager.syncCourseFromFirestore();

            // Sync class instances
            syncClassInstanceManager.syncClassInstanceToFirestore();
            syncClassInstanceManager.syncClassInstanceFromFirestore();

            // Sync notifications
            syncNotificationManager.syncNotificationsToFirestore();
            syncNotificationManager.syncNotificationsFromFirestore();

            DialogHelper.dismissLoadingDialog();

            // Notify all syncs
            DialogHelper.showSuccessDialog(this, "Sync data successfully!");
        }
    }

    @Override
    public void onLost() {
        isNetworkConnected = false;
        // Network disconnected
        Toast.makeText(this, "Wi-Fi Disconnected", Toast.LENGTH_SHORT).show();
    }
}