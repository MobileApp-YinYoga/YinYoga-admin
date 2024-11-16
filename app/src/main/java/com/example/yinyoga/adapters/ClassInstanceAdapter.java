package com.example.yinyoga.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.fragments.ManageClassInstancesFragment;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.service.ClassInstanceService;
import com.example.yinyoga.sync.SyncClassInstanceManager;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.ImageHelper;

import java.util.List;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.InstanceViewHolder> {
    private List<ClassInstance> instanceList;
    private final ManageClassInstancesFragment fragment;
    private final ClassInstanceService instanceService;

    public ClassInstanceAdapter(List<ClassInstance> instanceList, ManageClassInstancesFragment fragment) {
        this.fragment = fragment;
        this.instanceList = instanceList;
        this.instanceService = new ClassInstanceService(fragment.getContext());
    }

    @NonNull
    @Override
    public InstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_class_instance, parent, false);
        return new InstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstanceViewHolder holder, int position) {
        ClassInstance instance = instanceList.get(position);

        // Format the CourseID like "Course - YG1001"
        holder.courseId.setText(String.format("%s #%s - %s ",
                instance.getCourse().getCourseName(),
                instance.getCourse().getCourseId(),
                instance.getInstanceId()));

        // Date: January, 20th, 2023 or "Date: N/A" if no value
        holder.date.setText(instance.getDate() != null && !instance.getDate().isEmpty()
                ? "Date: " + instance.getDate()
                : "Date: N/A");

        holder.teacher.setText(String.format("Teacher: %s", instance.getTeacher()));

        holder.classInstanceImage.setImageBitmap(ImageHelper.convertByteArrayToBitmap(instance.getImageUrl()));

        holder.instanceMenu.setOnClickListener(v -> showCustomPopupMenu(v, position));
    }

    private void showCustomPopupMenu(View anchorView, int position) {
        // Inflate the custom layout
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.menu_edit_delete, null);

        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Set up click listeners for menu items
        TextView editSection = popupView.findViewById(R.id.edit_section);
        TextView deleteSection = popupView.findViewById(R.id.delete_section);

        editSection.setOnClickListener(v -> {
            if (fragment != null) {
                String instanceId = instanceList.get(position).getInstanceId();
                fragment.openAddInstancePopup(instanceId);
            }
            popupWindow.dismiss();
        });

        deleteSection.setOnClickListener(v -> {
            DialogHelper.showDeleteConfirmationDialog(
                    fragment.getActivity(),
                    "Are you sure you want to delete class instance \"" + instanceList.get(position).getInstanceId() + "\"?",
                    new DialogHelper.DeleteConfirmationListener() {
                        @Override
                        public void onConfirm() {
                            fragment.loadInstancesFromDatabase();
                            SyncClassInstanceManager syncClassInstanceManager = new SyncClassInstanceManager(v.getContext());
                            syncClassInstanceManager.deleteClassInstance(instanceList.get(position).getInstanceId());
                            DialogHelper.showSuccessDialog(fragment.getActivity(), "Course removed successfully!");

                            // Xóa khóa học và làm mới danh sách
                            instanceService.deleteClassInstance(instanceList.get(position).getInstanceId());
                            instanceList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, instanceList.size());
                        }
                    });
            popupWindow.dismiss();
        });

        // Show the PopupWindow
        popupWindow.setElevation(10); // Set shadow nếu cần
        popupWindow.showAsDropDown(anchorView, 0, 30);
    }

    @Override
    public int getItemCount() {
        return instanceList.size();
    }

    public static class InstanceViewHolder extends RecyclerView.ViewHolder {
        TextView courseId, date, teacher;
        ImageView instanceMenu, classInstanceImage;

        public InstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tvDate);
            courseId = itemView.findViewById(R.id.courseId);
            teacher = itemView.findViewById(R.id.tvTeacher);
            instanceMenu = itemView.findViewById(R.id.instanceMenu);
            classInstanceImage = itemView.findViewById(R.id.class_instance_image);

            Log.d("InstanceViewHolder", "Date TextView: " + date);  // Check xem view này có bị null không
            Log.d("InstanceViewHolder", "CourseID TextView: " + courseId);  // Check xem view này có bị null không
            Log.d("InstanceViewHolder", "Teacher Spinner: " + teacher);  // Check xem view này có bị null không
            Log.d("InstanceViewHolder", "instanceMenu Spinner: " + instanceMenu);  // Check xem view này có bị null không
        }
    }

    public void updateInstanceList(List<ClassInstance> updatedList) {
        this.instanceList = updatedList;
        notifyDataSetChanged();
    }
}
