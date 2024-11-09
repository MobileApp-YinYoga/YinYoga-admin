package com.example.yinyoga.service;

import android.content.Context;
import android.database.Cursor;

import com.example.yinyoga.models.Course;
import com.example.yinyoga.repository.CourseRepository;

public class CourseService {
    private CourseRepository courseRepository;

    public CourseService(Context context) {
        this.courseRepository = new CourseRepository(context);
    }

    // Thêm khóa học mới với tất cả các thông tin chi tiết
    public void addCourse(String courseName, String dayOfWeek, String time, int capacity, int duration, double price, String courseType) {
        courseRepository.insertCourse(courseName, dayOfWeek, time, capacity, duration, price, courseType);
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
    public void updateCourse(int courseId, String courseName, String dayOfWeek, String time, int capacity, int duration, double price, String courseType) {
        courseRepository.updateCourse(courseId, courseName, dayOfWeek, time, capacity, duration, price, courseType);
    }

    // Xóa khóa học theo ID
    public void deleteCourse(int courseId) {
        courseRepository.deleteCourse(courseId);
    }
}
