package com.example.yinyoga.models;

public class Notification {
    private int id;
    private String email;
    private String title;
    private String description;
    private String time;
    private boolean isRead;
    private String createdDate;

    public Notification(int id, String email, String title, String description, String time, boolean isRead, String createdDate) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRead = isRead;
        this.createdDate = createdDate;
    }

    public Notification(String email, String title, String description, String time, boolean isRead, String createdDate) {
        this.email = email;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRead = isRead;
        this.createdDate = createdDate;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
