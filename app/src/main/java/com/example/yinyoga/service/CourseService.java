package com.example.yinyoga.service;

import android.content.Context;
import android.database.Cursor;

import com.example.yinyoga.models.Course;
import com.example.yinyoga.repository.CourseRepository;

public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(Context context) {
        this.courseRepository = new CourseRepository(context);
    }

    // Thêm khóa học mới với tất cả các thông tin chi tiết
    public void addCourse(String courseName, String courseType, String createdAt, String dayOfWeek, String description, int capacity, int duration, byte[] imageUrl, double price, String time) {
        courseRepository.insertCourse(courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageUrl, price, time);
    }

    // Lấy khóa học theo ID
    public Course getCourse(int courseId) {
        return courseRepository.getCourseById(courseId);
    }

    // Lấy tất cả các khóa học
    public Cursor getAllCourses() {
        return courseRepository.getAllCourses();
    }

    // Cập nhật khóa học
    public void updateCourse(int courseId, String courseName, String courseType, String createdAt, String dayOfWeek, String description, int capacity, int duration, byte[] imageUrl, double price, String time) {
        courseRepository.updateCourse(courseId, courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageUrl, price, time);
    }

    // Xóa khóa học theo ID
    public void deleteCourse(int courseId) {
        courseRepository.deleteCourse(courseId);
    }
}
