package com.example.yinyoga.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yinyoga.R;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.models.Role;
import com.example.yinyoga.models.User;
import com.example.yinyoga.repository.RoleRepository;
import com.example.yinyoga.service.UserService;
import com.example.yinyoga.utils.DialogHelper;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password, confirmPassword, fullName;
    private Button registerButton;
    private ImageView ic_back;
    private UserService userService;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        userService = new UserService(this);  // Khởi tạo userService để thao tác với DB

        initView();  // Ánh xạ các view trong layout

        // Xử lý sự kiện khi nhấn nút đăng ký
        registerButton.setOnClickListener(v -> {
            // Kiểm tra tính hợp lệ của đầu vào
            if (verifyInput()) {
                processingRegister();  // Nếu hợp lệ, tiến hành đăng ký
            }
        });
    }

    // Xử lý đăng ký
    private void processingRegister() {
        String user = username.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String name = fullName.getText().toString().trim();

        // Mã hóa mật khẩu
        String hashedPassword = userService.hashPassword(pass);

        // Kiểm tra và thêm vai trò "Admin" nếu chưa tồn tại
        RoleRepository roleRepository = new RoleRepository(this);
        Role role = roleRepository.getRoleByName("Admin");
        if (role == null) {
            roleRepository.insertRole("Admin", "Administrator");
            role = roleRepository.getRoleByName("Admin"); // Lấy lại vai trò sau khi thêm
        }

        // Thêm user vào cơ sở dữ liệu
        userService.addUser(user, name, mail, hashedPassword, role.getRoleId());

        // Hiển thị thông báo thành công và chuyển sang màn hình đăng nhập
        DialogHelper.showSuccessDialog(this, "Register successful!");
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();  // Đóng Activity hiện tại
    }


    // Kiểm tra tính hợp lệ của thông tin đầu vào
    private boolean verifyInput() {
        boolean isValid = true;

        String user = username.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();
        String name = fullName.getText().toString().trim();

        // Kiểm tra username
        if (user.isEmpty()) {
            username.setError("Username cannot be empty.");
            isValid = false;
        } else if (!user.matches("^[a-zA-Z0-9._-]{3,15}$")) {
            username.setError("Username must be 3-15 characters long and contain only letters, digits, underscores, or dashes.");
            isValid = false;
        } else if (userService.checkUsernameExists(user)) {
            username.setError("Username already exists, please choose another.");
            isValid = false;
        }

        // Kiểm tra email
        if (mail.isEmpty()) {
            email.setError("Email cannot be empty.");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Invalid email format, please enter a valid email.");
            isValid = false;
        } else if (userService.checkEmailExists(mail)) {
            email.setError("This email is already registered.");
            isValid = false;
        }

        // Kiểm tra password
        if (pass.isEmpty()) {
            password.setError("Password cannot be empty.");
            isValid = false;
        } else if (pass.length() < 6) {
            password.setError("Password must be at least 6 characters long.");
            isValid = false;
        } else if (!pass.matches(".*[A-Z].*")) {
            password.setError("Password must contain at least one uppercase letter.");
            isValid = false;
        } else if (!pass.matches(".*[a-z].*")) {
            password.setError("Password must contain at least one lowercase letter.");
            isValid = false;
        } else if (!pass.matches(".*\\d.*")) {
            password.setError("Password must contain at least one digit.");
            isValid = false;
        } else if (!pass.matches(".*[!@#$%^&*+=?-].*")) {
            password.setError("Password must contain at least one special character (e.g. !@#$%^&*+=?-).");
            isValid = false;
        }

        // Kiểm tra confirm password khớp với password
        if (!pass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match.");
            isValid = false;
        }

        // Kiểm tra tên đầy đủ
        if (name.isEmpty()) {
            fullName.setError("Full name cannot be empty.");
            isValid = false;
        } else if (!name.matches("^[a-zA-Z ]+$")) {
            fullName.setError("Full name can only contain letters and spaces.");
            isValid = false;
        }

        // Nếu tất cả hợp lệ, trả về true
        return isValid;
    }

    // Khởi tạo các view
    private void initView() {
        userList = new ArrayList<>();

        username = findViewById(R.id.register_username);
        fullName = findViewById(R.id.register_fullname);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirmPassword = findViewById(R.id.register_confirm_password);
        registerButton = findViewById(R.id.register_button);
        ic_back = findViewById(R.id.ic_back);

        ic_back.setOnClickListener(v -> finish());
    }
}
