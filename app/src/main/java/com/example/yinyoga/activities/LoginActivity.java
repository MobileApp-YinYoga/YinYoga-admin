package com.example.yinyoga.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yinyoga.R;
import com.example.yinyoga.models.User;
import com.example.yinyoga.service.UserService;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.UserSessionManager;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private TextView error_text, forget_pass, nav_register;
    private Button loginButton;
    private ImageView ic_back;
    private UserService userService;
    private CheckBox rememberMeCheckBox;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        initView();
        checkRememberMe();
        setupListeners();
    }

    private void initView() {
        userService = new UserService(this);
        sessionManager = new UserSessionManager(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forget_pass = findViewById(R.id.forget_pass);
        loginButton = findViewById(R.id.btnSubmit);
        ic_back = findViewById(R.id.ic_back);
        error_text = findViewById(R.id.error_text);
        rememberMeCheckBox = findViewById(R.id.remember_me);
        nav_register = findViewById(R.id.nav_register);

        error_text.setVisibility(TextView.GONE);
        applyCheckboxColorState();
    }

    private void applyCheckboxColorState() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };

        int[] colors = new int[]{
                ContextCompat.getColor(this, R.color.main),
                ContextCompat.getColor(this, R.color.unchecked_gray)
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        rememberMeCheckBox.setButtonTintList(colorStateList);
    }

    private void checkRememberMe() {
        if (sessionManager.isRemembered() && userService.getUser(sessionManager.getUsername()) != null) {
            User sessionUser = sessionManager.getCurrentUser();
            if (sessionUser != null) {
                username.setText(sessionUser.getUsername());
                password.setText(sessionUser.getPassword());
            }
        } else {
            rememberMeCheckBox.setChecked(false);
        }
    }

    private void setupListeners() {
        ic_back.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, WelcomeActivity.class)));
        forget_pass.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
        nav_register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        loginButton.setOnClickListener(v -> processLogin());
    }

    private void processLogin() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        try {
            userService.authenticateUser(user, pass); // Thực hiện xác thực

            // Nếu xác thực thành công, lưu trạng thái đăng nhập
            User storedUser = userService.getUser(user);
            sessionManager.saveLoginState(
                    storedUser.getUsername(),
                    storedUser.getFullName(),
                    storedUser.getEmail(),
                    pass,
                    storedUser.getRole().getDescription(),
                    rememberMeCheckBox.isChecked()
            );

            // Chuyển đến MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        } catch (Exception e) {
            // Xử lý ngoại lệ và hiển thị lỗi lên giao diện
            error_text.setVisibility(TextView.VISIBLE);
            error_text.setText(e.getMessage());
        }

        logAllUsers();
    }

    private void logAllUsers() {
        List<User> userList = userService.getAllUsers();

        if (userList == null) {
            DialogHelper.showErrorDialog(this, "Database doesn't have any user!");
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            return;
        }

        for (User user : userList) {
            Log.d("username: ", user.getUsername());
            Log.d("name: ", user.getFullName());
            Log.d("email: ", user.getEmail());
            Log.d("password: ", user.getPassword());
            Log.d("role: ", user.getRole().getDescription());
        }
    }
}
