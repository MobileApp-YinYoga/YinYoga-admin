package com.example.yinyoga.adapters;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.utils.DialogHelper;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private final List<String> dateList;
    private final DialogHelper.OnDateSelectedListener listener;
    private final Dialog dialog;

    public DateAdapter(List<String> dateList, DialogHelper.OnDateSelectedListener listener, Dialog dialog) {
        this.dateList = dateList;
        this.listener = listener;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        String date = dateList.get(position);
        holder.dateTextView.setText(date);

        holder.itemView.setOnClickListener(v -> {
            listener.onDateSelected(date);
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.textDate);
        }
    }
}
