package com.example.yinyoga.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.yinyoga.R;
import com.example.yinyoga.models.User;
import com.example.yinyoga.service.UserService;
import com.example.yinyoga.sync.SyncClassInstanceManager;
import com.example.yinyoga.sync.SyncCourseManager;
import com.example.yinyoga.sync.SyncNotificationManager;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.UserSessionManager;

public class ManageUserActivity extends AppCompatActivity {
    private ImageView backHome;
    private TextView tvUsername, tvEmail, tvFullname, tvTitle;
    private EditText edUsername, edEmail, edFullName;
    private LinearLayout editProfileLayout, changePasswordLayout, submitLayout, generalLayout;
    private UserService userService;
    private UserSessionManager sessionManager;
    private String username;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_manage_user), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the status bar background color to white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        // Set the status bar icon to be black (light status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();

        backHome.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUserActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        setupListeners();
        loadUserData();
    }

    private void initView() {
        sessionManager = new UserSessionManager(this);
        userService = new UserService(this);

        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvFullname = findViewById(R.id.tv_full_name);
        tvTitle = findViewById(R.id.tv_title);

        editProfileLayout = findViewById(R.id.edit_profile);
        changePasswordLayout = findViewById(R.id.change_password);
        generalLayout = findViewById(R.id.general);
        submitLayout = findViewById(R.id.linear_class_capacity);

        edUsername = findViewById(R.id.edit_profile_username);
        edEmail = findViewById(R.id.edit_profile_email);
        edFullName = findViewById(R.id.edit_profile_fullname);

        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.cancel);
        backHome = findViewById(R.id.profile_back_icon);

        username = sessionManager.getUsername();
    }

    private void setupListeners() {
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> showSection("profile"));
        findViewById(R.id.btn_change_password).setOnClickListener(v -> showSection("change_password"));
        findViewById(R.id.btn_reset_database).setOnClickListener(v -> showSection("reset_database"));
        findViewById(R.id.general).setOnClickListener(v -> showSection("general"));

        btnSave.setOnClickListener(v -> {
            String title = tvTitle.getText().toString();
            if ("Change Password".equals(title)) {
                changePassword();
            } else if ("Edit Profile".equals(title)) {
                editProfile();
            }
        });

        btnCancel.setOnClickListener(v -> showSection("general"));
    }

    private void loadUserData() {
        tvUsername.setText(sessionManager.getUsername());
        tvEmail.setText(sessionManager.getEmail());
        tvFullname.setText(sessionManager.getFullName());
    }

    private void showSection(String action) {
        editProfileLayout.setVisibility(View.GONE);
        changePasswordLayout.setVisibility(View.GONE);
        generalLayout.setVisibility(View.GONE);
        submitLayout.setVisibility(View.GONE);

        switch (action) {
            case "profile":
                editProfileLayout.setVisibility(View.VISIBLE);
                submitLayout.setVisibility(View.VISIBLE);
                tvTitle.setText("Edit Profile");
                fillUserData();
                break;
            case "change_password":
                changePasswordLayout.setVisibility(View.VISIBLE);
                submitLayout.setVisibility(View.VISIBLE);
                tvTitle.setText("Change Password");
                break;
            case "reset_database":
                showResetDatabaseDialog();
                break;
            default:
                generalLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void fillUserData() {
        User currentUser = sessionManager.getCurrentUser();
        Log.d("FillUserData", currentUser.getUsername());

        edUsername.setText(currentUser.getUsername());
        edEmail.setText(currentUser.getEmail());
        edFullName.setText(currentUser.getFullName());
    }

    private void editProfile() {
        String newUsername = edUsername.getText().toString().trim();
        String newEmail = edEmail.getText().toString().trim();
        String newFullName = edFullName.getText().toString().trim();

        if (!TextUtils.isEmpty(newUsername) && !TextUtils.isEmpty(newEmail) && !TextUtils.isEmpty(newFullName)) {
            try {
                User updatedUser = new User(newUsername, newFullName, newEmail);
                userService.updateUser(updatedUser, username);

                sessionManager.updateUsername(newUsername);
                sessionManager.updateEmail(newEmail);
                sessionManager.updateFullName(newFullName);

                DialogHelper.showSuccessDialog(this, "Profile updated successfully.");
                showSection("general");

                Intent intent = new Intent("USER_PROFILE_UPDATED");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                loadUserData();
            } catch (IllegalArgumentException e) {
                DialogHelper.showErrorDialog(this, e.getMessage());
            }
        } else {
            DialogHelper.showErrorDialog(this, "Please fill in all fields");
        }
    }

    private void showResetDatabaseDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_notification_reset_database);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView dialogMessage = dialog.findViewById(R.id.dialog_subtitle);
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        LinearLayout confirmNotification = dialog.findViewById(R.id.confirm_notification);
        LinearLayout confirmPassword = dialog.findViewById(R.id.confirm_password);
        Button btnYes = dialog.findViewById(R.id.btn_yes);
        Button btnNo = dialog.findViewById(R.id.btn_no);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        EditText etPassword = dialog.findViewById(R.id.et_password);

        btnYes.setOnClickListener(v -> {
            confirmNotification.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.VISIBLE);
            dialogTitle.setText("Security Confirmation Required");
            dialogMessage.setText("Please enter your administrator password to confirm the database reset.");
        });

        btnNo.setOnClickListener(v -> {
            showSection("general");
            dialog.dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            String enteredPassword = etPassword.getText().toString().trim();
            if (validatePassword(enteredPassword)) {
                resetDatabase();
                showSection("general");
                dialog.dismiss();
            } else {
                etPassword.setError("Incorrect password. Please try again.");
            }
        });

        dialog.show();
    }

    private void changePassword() {
        EditText etOldPassword = changePasswordLayout.findViewById(R.id.et_old_password);
        EditText etNewPassword = changePasswordLayout.findViewById(R.id.et_new_password);
        EditText etConfirmNewPassword = changePasswordLayout.findViewById(R.id.et_confirm_new_password);

        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            etOldPassword.setError("Current password is required");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            etConfirmNewPassword.setError("Passwords do not match");
            return;
        }

        User user = userService.getUser(sessionManager.getUsername());
        if (userService.verifyPassword(oldPassword, user.getPassword())) {
            String passwordHash = userService.hashPassword(newPassword);
            userService.updatePassword(passwordHash, username);

            sessionManager.updatePassword(newPassword);
            etOldPassword.setText("");
            etNewPassword.setText("");
            etConfirmNewPassword.setText("");
            DialogHelper.showSuccessDialog(this, "Password changed successfully.");
        } else {
            etOldPassword.setError("Incorrect current password");
        }
    }

    private boolean validatePassword(String password) {
        User user = userService.getUser(sessionManager.getUsername());
        return userService.verifyPassword(password, user.getPassword());
    }

    private void resetDatabase() {
        userService.resetDatabase();

        new SyncClassInstanceManager(this).resetClassInstanceInFirestore();
        new SyncCourseManager(this).resetCourseInFirestore();
        new SyncNotificationManager(this).resetNotificationInFirestore();

        Toast.makeText(this, "Database reset successfully", Toast.LENGTH_SHORT).show();
    }
}
