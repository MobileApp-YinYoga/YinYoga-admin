package com.example.yinyoga.service;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import com.example.yinyoga.models.User;
import com.example.yinyoga.repository.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserService {
    private UserRepository userRepository;

    public UserService(Context context) {
        this.userRepository = new UserRepository(context);
    }

    // Thêm người dùng mới
    public void addUser(String username, String fullName, String email, String password, int roleId) {
        // Kiểm tra xem username hoặc email đã tồn tại chưa
        if (checkUsernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (checkEmailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        // Thêm người dùng vào cơ sở dữ liệu
        userRepository.insertUser(username, fullName, email, password, roleId);
    }



    // Kiểm tra xem username đã tồn tại chưa
    public boolean checkUsernameExists(String username) {
        User user = userRepository.getUserById(username);
        return user != null; // Trả về true nếu người dùng đã tồn tại
    }

    // Kiểm tra xem email đã tồn tại chưa
    public boolean checkEmailExists(String email) {
        User user = userRepository.getUserByEmail(email);
        return user != null; // Trả về true nếu email đã tồn tại
    }

    // Lấy người dùng theo username
    public User getUser(String username) {
        return userRepository.getUserById(username);
    }

    public User getUserByMail(String mail) {
        return userRepository.getUserByEmail(mail);
    }

    // Lấy tất cả người dùng dưới dạng Cursor
    public Cursor getAllUsers() {
        return userRepository.getAllUsers();
    }

    // Cập nhật thông tin người dùng
    public void updateUser(User user, String oldUsername) {
        // Trước khi cập nhật, kiểm tra email có tồn tại không nhưng bỏ qua email của người dùng hiện tại
        User currentUser = userRepository.getUserById(oldUsername);
        if (currentUser == null) {
            throw new IllegalArgumentException("Username does not exist");
        }
        userRepository.updateUser(user.getUsername(), user.getFullName(), user.getEmail(), oldUsername);
    }

    // Xóa người dùng
    public void deleteUser(String username) {
        userRepository.deleteUser(username);
    }


    // Thay đổi mật khẩu
    public void updatePassword(String newPassword, String username) {
        userRepository.updatePassword(username, newPassword);
    }

    // Phương thức mã hóa mật khẩu với PBKDF2
    public String hashPassword(String password) {
        try {
            int iterations = 10000;
            int keyLength = 256;
            byte[] salt = getSalt();

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = keyFactory.generateSecret(spec).getEncoded();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
            }

            return "";

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    // Tạo một salt ngẫu nhiên
    private byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    // Xác thực người dùng (đăng nhập)
    public void authenticateUser(String username, String password) throws Exception {
        User user = userRepository.getUserById(username);

        if (user == null) {
            throw new Exception("Username does not exist"); // Nếu tên người dùng không tồn tại
        }
        if (!verifyPassword(password, user.getPassword())) {
            throw new Exception("Incorrect password"); // Nếu mật khẩu không chính xác
        }
    }

    // Hàm xác thực mật khẩu
    public boolean verifyPassword(String enteredPassword, String storedPasswordHash) {
        try {
            String[] parts = storedPasswordHash.split(":");
            byte[] salt = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                salt = Base64.getDecoder().decode(parts[0]);
            }
            byte[] hash = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                hash = Base64.getDecoder().decode(parts[1]);
            }

            PBEKeySpec spec = new PBEKeySpec(enteredPassword.toCharArray(), salt, 10000, 256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = keyFactory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }
}
