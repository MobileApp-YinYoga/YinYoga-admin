package com.example.yinyoga.service;

import android.content.Context;
import android.database.Cursor;

import com.example.yinyoga.models.Course;
import com.example.yinyoga.repository.CourseRepository;

import java.util.List;

public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(Context context) {
        this.courseRepository = new CourseRepository(context);
    }

    public void addCourse(Course course) {
        courseRepository.insertCourse(course);
    }

    public Course getCourse(int courseId) {
        return courseRepository.getCourseById(courseId);
    }

    public List<Course> getAllCourses() {
        return courseRepository.getAllCourses();
    }

    public void updateCourse(Course course) {
        courseRepository.updateCourse(course);
    }

    public void deleteCourse(int courseId) {
        courseRepository.deleteCourse(courseId);
    }
}
