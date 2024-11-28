package com.example.yinyoga.service;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import com.example.yinyoga.models.User;
import com.example.yinyoga.repository.UserRepository;
import com.example.yinyoga.sync.SyncClassInstanceManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserService {
    private UserRepository userRepository;

    public UserService(Context context) {
        this.userRepository = new UserRepository(context);
    }

    public void resetDatabase() {
        userRepository.resetDatabase();
    }

    public void addUser(String username, String fullName, String email, String password, int roleId) {
        if (checkUsernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (checkEmailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        userRepository.insertUser(username, fullName, email, password, roleId);
    }

    public boolean checkUsernameExists(String username) {
        User user = userRepository.getUserById(username);
        return user != null;
    }

    public boolean checkEmailExists(String email) {
        User user = userRepository.getUserByEmail(email);
        return user != null;
    }

    public User getUser(String username) {
        return userRepository.getUserById(username);
    }

    public User getUserByMail(String mail) {
        return userRepository.getUserByEmail(mail);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public void updateUser(User user, String oldUsername) {
        User currentUser = userRepository.getUserById(oldUsername);
        if (currentUser == null) {
            throw new IllegalArgumentException("Username does not exist");
        }
        userRepository.updateUser(user.getUsername(), user.getFullName(), user.getEmail(), oldUsername);
    }

    public void deleteUser(String username) {
        userRepository.deleteUser(username);
    }

    public void updatePassword(String newPassword, String username) {
        userRepository.updatePassword(username, newPassword);
    }

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

    private byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public void authenticateUser(String username, String password) throws Exception {
        User user = userRepository.getUserById(username);

        if (user == null) {
            throw new Exception("Username does not exist");
        }
        if (!verifyPassword(password, user.getPassword())) {
            throw new Exception("Incorrect password");
        }
    }

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
