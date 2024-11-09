package com.example.yinyoga.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.models.Course;

import java.util.List;

public class MenuCourseAdapter extends RecyclerView.Adapter<MenuCourseAdapter.MenuCourseViewHolder> {
    private List<Course> courseList;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public MenuCourseAdapter(List<Course> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_search_course, parent, false);
        return new MenuCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseField.setText(course.getCourseName() + " #" + course.getCourseId());
        holder.itemView.setOnClickListener(v -> listener.onCourseClick(course));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class MenuCourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseField;

        MenuCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseField = itemView.findViewById(R.id.course_name_id);
        }
    }
}
