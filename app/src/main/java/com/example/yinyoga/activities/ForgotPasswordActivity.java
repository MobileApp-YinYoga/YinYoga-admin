package com.example.yinyoga.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.yinyoga.R;
import com.example.yinyoga.models.User;
import com.example.yinyoga.service.UserService;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.JavaMailAPI;

import java.util.Random;
import java.util.concurrent.Executors;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageView ic_back;
    private EditText edEmail, edVerifyCode, edNewPassword, edConfirmPassword;
    private TextView errorText, title;
    private Button btnSubmit, btnChangePassword;
    private LinearLayout resetPasswordLayout;
    private String verificationCode;
    private UserService userService;
    private User currentUser = new User();

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_pass);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        initView();
        ic_back.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            String action = btnSubmit.getText().toString().trim();
            if (action.equals("Verify")) {
                checkVerifyCode();
            } else {
                String email = edEmail.getText().toString().trim();
                if (verifyEmail(email)) {
                    DialogHelper.showLoadingDialog(this, "Sending email...");
                    sendVerificationCode(email);
                }
            }
        });

        // Change password button action
        btnChangePassword.setOnClickListener(v -> setNewPassword());
    }

    private String generateVerificationCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    private void sendVerificationCode(String recipientEmail) {
        verificationCode = generateVerificationCode();
        String subject = "Password Reset Request";
        String message = "Dear " + currentUser.getFullName() + ",\n\n"
                + "We received a request to reset your password for your YinYoga account. "
                + "Please use the following verification code to proceed with resetting your password:\n\n"
                + "Verification Code: " + verificationCode + "\n\n"
                + "If you did not request a password reset, please ignore this email. Your account will remain secure.\n\n"
                + "Thank you,\n"
                + "The YinYoga Support Team";

        JavaMailAPI javaMailAPI = new JavaMailAPI(recipientEmail, subject, message);
        javaMailAPI.setOnEmailSentListener(success -> runOnUiThread(() -> {
            if (success) {
                DialogHelper.showSuccessDialog(this, "Email sent successfully!");
                DialogHelper.dismissLoadingDialog();

                edEmail.setVisibility(View.GONE);
                edVerifyCode.setVisibility(View.VISIBLE);
                title.setText("Verify Your Email");
                btnSubmit.setText("Verify");
            } else {
                DialogHelper.showErrorDialog(this, "Failed to send email");
            }
        }));

        Executors.newSingleThreadExecutor().execute(javaMailAPI);
    }

    private boolean verifyEmail(String email) {
        if (email.isEmpty()) {
            errorText.setText("Email cannot be empty.");
            errorText.setVisibility(View.VISIBLE);
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorText.setText("Invalid email format.");
            errorText.setVisibility(View.VISIBLE);
            return false;
        }

        if (!userService.checkEmailExists(email)) {
            errorText.setText("This email is not registered.");
            errorText.setVisibility(View.VISIBLE);
            return false;
        }

        currentUser = userService.getUserByMail(email);
        errorText.setVisibility(View.GONE);

        return true;
    }

    private void checkVerifyCode() {
        String inputCode = edVerifyCode.getText().toString().trim();

        if (inputCode.equals(verificationCode)) {
            errorText.setVisibility(View.GONE);
            edVerifyCode.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
            resetPasswordLayout.setVisibility(View.VISIBLE);
        } else {
            errorText.setText("Invalid verification code. Please check your email.");
            errorText.setVisibility(View.VISIBLE);
        }
    }

    private void setNewPassword() {
        String newPassword = edNewPassword.getText().toString().trim();
        String confirmPassword = edConfirmPassword.getText().toString().trim();

        if (isValidPassword(newPassword, confirmPassword)){
            // Băm mật khẩu mới
            String hashedPassword = userService.hashPassword(newPassword);

            // Đặt mật khẩu mới cho người dùng
            userService.updatePassword(currentUser.getEmail(), hashedPassword);

            DialogHelper.showSuccessDialog(this, "Password set successfully!");
            new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        }
    }

    private boolean isValidPassword(String newPassword, String confirmPassword) {
        boolean isValid = true;

        // Kiểm tra độ dài mật khẩu mới
        if (newPassword.isEmpty() || newPassword.length() < 6) {
            errorText.setText("New password must be at least 6 characters.");
            errorText.setVisibility(View.VISIBLE);

            isValid = false;
        }

        if (userService.verifyPassword(newPassword, currentUser.getPassword())) {
            errorText.setText("New password cannot be the same as the old one.");
            errorText.setVisibility(View.VISIBLE);

            isValid = false;
        }

        // Kiểm tra khớp mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            errorText.setText("Passwords do not match.");
            errorText.setVisibility(View.VISIBLE);

            isValid = false;
        }

        errorText.setVisibility(View.GONE);
        return isValid;
    }


    private void initView() {
        userService = new UserService(this);
        ic_back = findViewById(R.id.ic_back);
        title = findViewById(R.id.title);
        edEmail = findViewById(R.id.email);
        edVerifyCode = findViewById(R.id.verify_code);
        edNewPassword = findViewById(R.id.new_password);
        edConfirmPassword = findViewById(R.id.confirm_password);
        errorText = findViewById(R.id.error_text);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnChangePassword = findViewById(R.id.change_password_button);
        resetPasswordLayout = findViewById(R.id.reset_password);

        errorText.setVisibility(View.GONE);
        resetPasswordLayout.setVisibility(View.GONE); // Ẩn layout đổi mật khẩu ban đầu
    }
}
