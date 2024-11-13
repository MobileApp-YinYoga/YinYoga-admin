package com.example.yinyoga.models;

public class Course {
    private int courseId;
    private String courseName;
    private String courseType;
    private String createdAt;
    private String dayOfWeek;
    private String description;
    private int capacity;
    private int duration;
    private byte[] imageUrl;
    private double price;
    private String time;

    public Course() {
    }

    public Course(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    // Constructor
    public Course(int courseId, String courseName, String courseType, String createdAt,
                  String dayOfWeek, String description, int capacity, int duration,
                  byte[] imageUrl, double price, String time) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseType = courseType;
        this.createdAt = createdAt;
        this.dayOfWeek = dayOfWeek;
        this.description = description;
        this.capacity = capacity;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.price = price;
        this.time = time;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public byte[] getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(byte[] imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
