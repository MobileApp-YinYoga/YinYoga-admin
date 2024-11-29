package com.example.yinyoga.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.utils.ImageHelper;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ClassInstanceViewHolder> {

    private List<ClassInstance> classInstanceList;
    private Context context;

    public DetailAdapter(List<ClassInstance> ClassInstanceList, Context context) {
        this.classInstanceList = ClassInstanceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_booking_detail, parent, false);
        return new ClassInstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassInstanceViewHolder holder, int position) {
        ClassInstance classInstance = classInstanceList.get(position);

        // Set data to the views
        holder.courseNameInstanceId.setText(classInstance.getCourse().getCourseName() + " - " + classInstance.getInstanceId());
        holder.date.setText("Date: " + classInstance.getDate());
        holder.teacher.setText("Teacher: " + classInstance.getTeacher());

        // Convert byte[] imageUrl to Bitmap and set it to ImageView
        if (classInstance.getImageUrl() != null) {
            Bitmap bitmap = ImageHelper.convertByteArrayToBitmap(classInstance.getImageUrl());
            holder.classInstanceImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return classInstanceList.size();
    }

    public static class ClassInstanceViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameInstanceId, date, teacher;
        ImageView classInstanceImage;

        public ClassInstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameInstanceId = itemView.findViewById(R.id.booking_detail_course_name_instanceId);
            date = itemView.findViewById(R.id.booking_detail_date);
            teacher = itemView.findViewById(R.id.booking_detail_teacher);
            classInstanceImage = itemView.findViewById(R.id.booking_detail_image);
        }
    }
}
