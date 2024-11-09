package com.example.yinyoga.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.example.yinyoga.database.Database;
import com.example.yinyoga.fragments.ManageClassInstancesFragment;
import com.example.yinyoga.fragments.ManageCoursesFragment;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.UserSessionManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ImageView menuIcon, notificationIcon;
    private Database database;
    private TextView username, roleName;



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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        initView();
        authentication(savedInstanceState);
        setEventNotification();
    }

    private void initView() {
        // Khởi tạo database
        database = new Database(this);
        SQLiteDatabase db = database.getWritableDatabase();

        // Khởi tạo các view từ layout
        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menu_icon);
        notificationIcon = findViewById(R.id.notification_icon);
        navigationView = findViewById(R.id.navigation_view);

        // Thiết lập Drawer Toggle để mở và đóng thanh điều hướng
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Truy cập vào NavigationView
        navigationView = findViewById(R.id.navigation_view);

        // Lấy header view từ NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Truy cập vào các TextView trong nav_header
        username = headerView.findViewById(R.id.user_name);
        roleName = headerView.findViewById(R.id.user_role);
    }

    private void authentication(Bundle savedInstanceState) {
        UserSessionManager sessionManager = new UserSessionManager(this);

        // Kiểm tra nếu người dùng đã đăng nhập
        if (sessionManager.isLoggedIn()) {
            username.setText(sessionManager.getUsername());
            roleName.setText(sessionManager.getRole());

            setEventMenu(savedInstanceState); // Thiết lập sự kiện cho menu (nếu có)
        } else {
            // Nếu chưa đăng nhập, chuyển về LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Đóng MainActivity để người dùng không quay lại
        }
    }


    private void setEventMenu(Bundle savedInstanceState) {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START); // Đóng nếu đang mở
                } else {
                    drawerLayout.openDrawer(GravityCompat.START); // Mở thanh điều hướng
                }
            }
        });

        // Thiết lập sự kiện khi chọn item từ thanh điều hướng
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId(); // Lấy id của item được chọn

                if (id == R.id.nav_statistic) {
//                    selectedFragment = new StatisticFragment();
                } else if (id == R.id.nav_manage_classes) {
                    selectedFragment = new ManageCoursesFragment();
                } else if (id == R.id.nav_manage_class_instances) {
                    selectedFragment = new ManageClassInstancesFragment();
                }else if (id == R.id.nav_profile) {
                    // Khởi chạy Activity cho trang hồ sơ người dùng
                    Intent intent = new Intent(MainActivity.this, ManageUserActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_notifications) {
                    // Khởi chạy Activity cho trang thông báo
                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    DialogHelper.showDeleteConfirmationDialog(
                            MainActivity.this,
                            "Are you sure you want to log out?",
                            new DialogHelper.DeleteConfirmationListener() {
                                @Override
                                public void onConfirm() {
                                    // Thực hiện đăng xuất khi người dùng xác nhận
                                    UserSessionManager sessionManager = new UserSessionManager(MainActivity.this);
                                    sessionManager.logout();

                                    // Điều hướng về LoginActivity
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish(); // Đóng MainActivity sau khi đăng xuất
                                }
                            }
                    );
                }

                // Thay thế Fragment trong FrameLayout nếu có Fragment được chọn
                if (selectedFragment != null) {
                    replaceFragment(selectedFragment);
                }

                drawerLayout.closeDrawer(GravityCompat.START); // Đóng thanh điều hướng sau khi chọn
                return true;
            }
        });

        // Mặc định mở ManageCoursesFragment khi mở ứng dụng
        if (savedInstanceState == null) {
            replaceFragment(new ManageCoursesFragment()); // Hiển thị ManageCoursesFragment mặc định
        }

    }


    private void setEventNotification() {
        // Xử lý sự kiện khi nhấn vào biểu tượng chuông thông báo
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện hành động khi nhấn vào biểu tượng thông báo
                // Ví dụ: mở trang thông báo
                DialogHelper.showSuccessDialog(MainActivity.this, "Your work has been saved!");
            }
        });
    }

    // Phương thức để thay thế Fragment trong FrameLayout
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}