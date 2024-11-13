package com.example.yinyoga.service;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.repository.ClassInstanceRepository;

import java.util.ArrayList;
import java.util.List;

public class ClassInstanceService {
    private ClassInstanceRepository classInstanceRepository;

    public ClassInstanceService(Context context) {
        this.classInstanceRepository = new ClassInstanceRepository(context);
    }

    public void addClassInstance(String instanceId, int courseId, String date, String teacher, byte[] imageUrl) {
        try {
            classInstanceRepository.insertClassInstance(instanceId, courseId, date, teacher, imageUrl);
        } catch (Exception e) {
            Log.e("addClassInstance", "Error adding class instance: " + e.getMessage()); // Ghi log lỗi
        }
    }

    // Lấy phiên học theo ID
    public ClassInstance getClassInstance(String instanceId) {
        try {
            return classInstanceRepository.getClassInstanceById(instanceId);
        } catch (Exception e) {
            Log.e("getClassInstance", "Error retrieving class instance by ID: " + e.getMessage());
            return null; // Trả về null nếu có lỗi
        }
    }

    // Lấy phiên học theo Course ID
    public List<ClassInstance> getClassInstancesByCourseId(int courseId) {
        try {
            return classInstanceRepository.getClassInstancesByCourseId(courseId);
        } catch (Exception e) {
            Log.e("getClassInstancesByCourseId", "Error retrieving class instances by course ID: " + e.getMessage());
            return new ArrayList<>(); // Trả về một danh sách rỗng nếu có lỗi
        }
    }

    // Lấy tất cả các phiên học
    public Cursor getAllClassInstances() {
        try {
            return classInstanceRepository.getAllClassInstances();
        } catch (Exception e) {
            Log.e("getAllClassInstances", "Error retrieving all class instances: " + e.getMessage());
            return null; // Trả về null nếu có lỗi
        }
    }


    // Cập nhật phiên học
    public void updateClassInstance(String instanceId, int courseId, String date, String teacher, byte[] imageUrl) {
        try {
            classInstanceRepository.updateClassInstance(instanceId, courseId, date, teacher, imageUrl);
        } catch (Exception e) {
            Log.e("updateClassInstance", "Error updating class instance: " + e.getMessage()); // Ghi log lỗi
        }
    }

    // Xóa phiên học
    public void deleteClassInstance(String instanceId) {
        try {
            classInstanceRepository.deleteClassInstance(instanceId);
        } catch (Exception e) {
            Log.e("deleteClassInstance", "Error deleting class instance: " + e.getMessage()); // Ghi log lỗi
        }
    }
}
