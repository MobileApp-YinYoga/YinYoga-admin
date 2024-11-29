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

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ClassInstanceViewHolder> {

    private List<ClassInstance> ClassInstanceList;
    private Context context;

    public DetailAdapter(List<ClassInstance> ClassInstanceList, Context context) {
        this.ClassInstanceList = ClassInstanceList;
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
        ClassInstance ClassInstance = ClassInstanceList.get(position);

        // Set data to the views
        holder.instanceId.setText("Class ID: " + ClassInstance.getInstanceId());
        holder.courseId.setText(ClassInstance.getCourse().getCourseId());
        holder.date.setText("Date: " + ClassInstance.getDate());
        holder.teacher.setText("Teacher: " + ClassInstance.getTeacher());

        // Convert byte[] imageUrl to Bitmap and set it to ImageView
        if (ClassInstance.getImageUrl() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(ClassInstance.getImageUrl(), 0, ClassInstance.getImageUrl().length);
            holder.classInstanceImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return ClassInstanceList.size();
    }

    public static class ClassInstanceViewHolder extends RecyclerView.ViewHolder {
        TextView instanceId, courseId, date, teacher;
        ImageView classInstanceImage;

        public ClassInstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            instanceId = itemView.findViewById(R.id.tvInstanceId);
            courseId = itemView.findViewById(R.id.courseId);
            date = itemView.findViewById(R.id.tvDate);
            teacher = itemView.findViewById(R.id.tvTeacher);
            classInstanceImage = itemView.findViewById(R.id.class_instance_image);
        }
    }
}
