package com.example.yinyoga.models;

public class ClassInstance {
    private String instanceId;
    private Course course;
    private String date;
    private String teacher;
    private byte[] imageUrl;

    public ClassInstance(String instanceId, Course course, String date, String teacher, byte[] imageUrl) {
        this.instanceId = instanceId;
        this.course = course;
        this.date = date;
        this.teacher = teacher;
        this.imageUrl = imageUrl;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public byte[] getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(byte[] imageUrl) {
        this.imageUrl = imageUrl;
    }
}
