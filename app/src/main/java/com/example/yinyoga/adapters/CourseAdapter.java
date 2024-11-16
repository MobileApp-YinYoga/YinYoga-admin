package com.example.yinyoga.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.fragments.ManageCoursesFragment;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.service.ClassInstanceService;
import com.example.yinyoga.service.CourseService;
import com.example.yinyoga.sync.SyncClassInstanceManager;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.ImageHelper;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.YogaClassViewHolder> {
    public interface CustomListeners {
        void onSeeMoreClick(int courseId);
    }

    private List<Course> courseList;
    private ManageCoursesFragment fragment;
    private CourseService courseService;
    private CustomListeners customListeners;

    public CourseAdapter(List<Course> courseList, ManageCoursesFragment fragment) {
        this.fragment = fragment;
        this.courseList = courseList;
        this.courseService = new CourseService(fragment.getContext());
    }

    public void setCustomListeners(CustomListeners customListeners) {
        this.customListeners = customListeners;
    }

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_course_layout, parent, false);
        return new YogaClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassViewHolder holder, int position) {
        Course course = courseList.get(position);

        // Set course details to the ViewHolder
        holder.className.setText(course.getCourseName() != null ? course.getCourseName() : "N/A");
        holder.dayOfWeek.setText(course.getDayOfWeek() != null ? course.getDayOfWeek() : "N/A");
        holder.time.setText(course.getTime() != null ? course.getTime() : "N/A");
        holder.duration.setText(course.getDuration() > 0 ? "  |  " + course.getDuration() + " minutes" : "N/A");
        holder.genre.setText(course.getCourseType() != null ? course.getCourseType() : "N/A");
        holder.price.setText(course.getPrice() > 0 ? course.getPrice() + " dollars" : "$0.00");
        holder.capacity.setText(course.getCapacity() > 0 ? "Capacity: " + course.getCapacity() + " members" : "Capacity: 0");

        // Set shortened description
        holder.description.setText(getShortenedDescription(course.getDescription()));

        // Load image if imageUrl is available
        holder.courseImage.setImageBitmap(ImageHelper.convertByteArrayToBitmap(course.getImageUrl()));

        holder.see_more_button.setOnClickListener(v -> {
            if (customListeners != null) {
                customListeners.onSeeMoreClick(course.getCourseId());
            }
        });

        // Open menu for edit or delete options
        holder.taskMenu.setOnClickListener(v -> showCustomPopupMenu(v, position));
    }

    private String getShortenedDescription(String description) {
        if (description == null || description.isEmpty()) {
            return "N/A";
        }
        String[] words = description.split(" ");
        int wordCount = Math.min(words.length, 4);
        StringBuilder shortenedDescription = new StringBuilder();

        for (int i = 0; i < wordCount; i++) {
            shortenedDescription.append(words[i]).append(" ");
        }

        if (words.length > 4) {
            shortenedDescription.append("...");
        }

        return shortenedDescription.toString().trim();
    }

    private void showCustomPopupMenu(View anchorView, int position) {
        // Inflate the custom layout
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.menu_edit_delete, null);

        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        TextView editSection = popupView.findViewById(R.id.edit_section);
        TextView deleteSection = popupView.findViewById(R.id.delete_section);

        // Set event for Edit button
        editSection.setOnClickListener(v -> handleEditAction(position, popupWindow));

        // Set event for Delete button
        deleteSection.setOnClickListener(v -> handleDeleteAction(position, popupWindow));

        // Show the PopupWindow
        popupWindow.setElevation(10); // Set shadow nếu cần
        popupWindow.showAsDropDown(anchorView, 0, 30);
    }

    private void handleEditAction(int position, PopupWindow popupWindow) {
        if (fragment != null) {
            int getIdCourse = courseList.get(position).getCourseId();
            fragment.openAddClassPopup(getIdCourse); // Open edit popup
        }
        popupWindow.dismiss(); // Close popup menu
    }

    // Method to handle delete action
    private void handleDeleteAction(int position, PopupWindow popupWindow) {
        DialogHelper.showDeleteConfirmationDialog(fragment.getActivity(), "Are you sure you want to delete course \"" + courseList.get(position).getCourseName() + "\"?", () -> {
//            SyncClassInstanceManager syncClassInstanceManager = new SyncClassInstanceManager(fragment.getContext());
//            syncClassInstanceManager.deleteCourse(courseList.get(position).getCourseId());
//            DialogHelper.showSuccessDialog(fragment.getActivity(), "Course removed successfully!");


            // Delete course and refresh list
            courseService.deleteCourse(courseList.get(position).getCourseId());
            courseList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, courseList.size());

            fragment.loadCourseFromDatabase();
            DialogHelper.showSuccessDialog(fragment.getActivity(), "Course removed successfully!");
        });
        popupWindow.dismiss(); // Close popup menu
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    // ViewHolder class for Course items
    public static class YogaClassViewHolder extends RecyclerView.ViewHolder {

        TextView className, dayOfWeek, time, duration, capacity, genre, price, description, see_more_button;
        ImageView taskMenu, courseImage;

        public YogaClassViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
            dayOfWeek = itemView.findViewById(R.id.day_of_the_week);
            time = itemView.findViewById(R.id.timeStart);
            duration = itemView.findViewById(R.id.duration);
            capacity = itemView.findViewById(R.id.capacity);
            genre = itemView.findViewById(R.id.genre);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            courseImage = itemView.findViewById(R.id.course_image);
            taskMenu = itemView.findViewById(R.id.courseMenu);
            see_more_button = itemView.findViewById(R.id.see_more_button);
        }
    }

    // Method to update the course list
    public void updateCourseList(List<Course> updatedList) {
        this.courseList = updatedList;  // Update the course list
        notifyDataSetChanged();  // Refresh the adapter
    }
}
