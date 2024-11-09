package com.example.yinyoga.models;

public class Notification {
    private int id;
    private String title;
    private String description;
    private String time;
    private boolean isRead;
    private String createdDate; // Ngày tạo thông báo

    public Notification(int id, String title, String description, String time, boolean isRead, String createdDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRead = isRead;
        this.createdDate = createdDate;
    }

    public Notification(String title, String description, String time, boolean isRead, String createdDate) {
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
