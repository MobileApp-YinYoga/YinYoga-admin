package com.example.yinyoga.models;

public class Course {
    private int courseId;        // Mã khóa học
    private String courseName;    // Tên khóa học
    private String dayOfWeek;     // Ngày diễn ra khóa học (ví dụ: Monday, Tuesday)
    private String time;          // Thời gian diễn ra khóa học
    private int capacity;         // Số lượng học viên tối đa
    private int duration;         // Thời lượng của lớp học (phút)
    private double price;         // Giá của lớp học
    private String courseType;    // Loại lớp học (Flow Yoga, Yin Yoga, etc.)

    public Course() {
    }

    public Course(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    // Constructor
    public Course(int courseId, String courseName, String dayOfWeek, String time, int capacity, int duration, double price, String courseType) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.courseType = courseType;
    }

    // Getters and Setters
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

}
