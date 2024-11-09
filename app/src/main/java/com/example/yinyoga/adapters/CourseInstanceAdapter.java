package com.example.yinyoga.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.fragments.ManageClassInstancesFragment;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.service.ClassInstanceService;
import com.example.yinyoga.utils.DialogHelper;

import java.util.List;

public class CourseInstanceAdapter extends RecyclerView.Adapter<CourseInstanceAdapter.InstanceViewHolder> {

    private List<ClassInstance> instanceList;
    private ManageClassInstancesFragment fragment;
    private ClassInstanceService instanceService;

    public CourseInstanceAdapter(List<ClassInstance> instanceList, ManageClassInstancesFragment fragment) {
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

        // Định dạng CourseID theo kiểu "Course - YG1001"
        holder.courseId.setText(String.format("%s #%s - %s ",
                instance.getCourse().getCourseName(),
                instance.getCourse().getCourseId(),
                instance.getInstanceId()));

        // Hiển thị Date: January, 20th, 2023 hoặc "Date: N/A" nếu không có giá trị
        holder.date.setText(instance.getDate() != null && !instance.getDate().isEmpty()
                ? "Date: " + instance.getDate()
                : "Date: N/A");

        // Hiển thị Teacher: Harry Potter
        holder.teacher.setText(String.format("Teacher: %s", instance.getTeacher()));

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
            // Thực hiện thao tác chỉnh sửa
            if (fragment != null) {
                String instanceId = instanceList.get(position).getInstanceId();
                fragment.openAddInstancePopup(instanceId); // Edit the course
            }
            popupWindow.dismiss(); // Đóng menu sau khi nhấn
        });

        deleteSection.setOnClickListener(v -> {
            // Xác nhận xóa
            DialogHelper.showDeleteConfirmationDialog(
                    fragment.getActivity(),
                    "Are you sure you want to delete class instance \"" + instanceList.get(position).getInstanceId() + "\"?",
                    new DialogHelper.DeleteConfirmationListener() {
                        @Override
                        public void onConfirm() {
                            // Xóa khóa học và làm mới danh sách
                            instanceService.deleteClassInstance(instanceList.get(position).getInstanceId());
                            instanceList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, instanceList.size());

                            fragment.loadInstancesFromDatabase();

                            DialogHelper.showSuccessDialog(fragment.getActivity(), "Course removed successfully!");
                        }
                    });
            popupWindow.dismiss(); // Đóng menu sau khi nhấn
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
        ImageView instanceMenu;

        public InstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tvDate);
            courseId = itemView.findViewById(R.id.courseId);
            teacher = itemView.findViewById(R.id.tvTeacher);
            instanceMenu = itemView.findViewById(R.id.instanceMenu);

            Log.d("InstanceViewHolder", "Date TextView: " + date);  // Check xem view này có bị null không
            Log.d("InstanceViewHolder", "CourseID TextView: " + courseId);  // Check xem view này có bị null không
            Log.d("InstanceViewHolder", "Teacher Spinner: " + teacher);  // Check xem view này có bị null không
            Log.d("InstanceViewHolder", "instanceMenu Spinner: " + instanceMenu);  // Check xem view này có bị null không

        }
    }

    // Phương thức cập nhật danh sách các phiên học
    public void updateInstanceList(List<ClassInstance> updatedList) {
        this.instanceList = updatedList;
        notifyDataSetChanged();
    }
}
