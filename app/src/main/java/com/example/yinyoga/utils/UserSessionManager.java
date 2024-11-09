package com.example.yinyoga.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.yinyoga.models.Role;
import com.example.yinyoga.models.User;

public class UserSessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_IS_REMEMBERED = "isRemembered";

    private static final String SESSION_UPDATE_ACTION = "SESSION_UPDATED"; // Action để phát tín hiệu cập nhật session

    private static UserSessionManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public UserSessionManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized UserSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserSessionManager(context);
        }
        return instance;
    }

    private void sendSessionUpdateBroadcast() {
        Intent intent = new Intent(SESSION_UPDATE_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // Lưu phiên đăng nhập
    public void saveLoginState(String username, String fullName, String email, String password, String role, boolean isRememberMe) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_IS_REMEMBERED, isRememberMe);

        if (isRememberMe) {
            editor.putString(KEY_PASSWORD, password);
        } else {
            editor.remove(KEY_PASSWORD);
        }

        editor.apply();
        sendSessionUpdateBroadcast();  // Phát tín hiệu sau khi lưu session
    }

    // Phương thức cập nhật từng thông tin cá nhân trong session
    public void updateUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
        sendSessionUpdateBroadcast();
    }

    public void updateFullName(String fullName) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.apply();
        sendSessionUpdateBroadcast();
    }

    public void updateEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
        sendSessionUpdateBroadcast();
    }

    public void updateRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
        sendSessionUpdateBroadcast();
    }

    public void updatePassword(String password) {
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
        sendSessionUpdateBroadcast();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isRemembered() {
        return sharedPreferences.getBoolean(KEY_IS_REMEMBERED, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public User getCurrentUser() {
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        String fullName = sharedPreferences.getString(KEY_FULL_NAME, null);
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String roleName = sharedPreferences.getString(KEY_ROLE, null);
        String password = sharedPreferences.getString(KEY_PASSWORD, null);

        if (username != null && (password != null || isRemembered())) {
            return new User(username, fullName, email, password, new Role(roleName));
        } else {
            return null;
        }
    }

    public void logout() {
        if (!isRemembered()) {
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_FULL_NAME);
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_ROLE);
            editor.remove(KEY_PASSWORD);
            editor.putBoolean(KEY_IS_REMEMBERED, false);
        }

        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        sendSessionUpdateBroadcast();  // Phát tín hiệu sau khi đăng xuất
    }
}
