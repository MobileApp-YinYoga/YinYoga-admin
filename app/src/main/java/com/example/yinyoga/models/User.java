package com.example.yinyoga.models;

public class User {
        private String username;  // Mã định danh lớp học
        private String fullName;  // Tên lớp học
        private String email;  // Số lượng học viên tối đa
        private String password;  // Thời lượng của lớp học (phút)
        private Role role;

    public User(String username, String fullName, String email, String password, Role role) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String newUsername, String newFullName, String newEmail) {
        this.username = newUsername;
        this.fullName = newFullName;
        this.email = newEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
