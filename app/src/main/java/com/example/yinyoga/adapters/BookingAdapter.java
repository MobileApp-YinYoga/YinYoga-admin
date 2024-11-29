package com.example.yinyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.models.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;

    public BookingAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_manage_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Set data to the views
        holder.bookingId.setText("Booking ID: " + booking.getBookingId());
        holder.email.setText("Email: " + booking.getEmail());
        holder.bookingDate.setText("Booking Date: " + booking.getBookingDate());
        holder.status.setText("Status: " + booking.getStatus());
        holder.totalAmount.setText("Total Amount: " + booking.getTotalAmount());

//        BookingDetailAdapter detailAdapter = new BookingDetailAdapter(booking.getBookingDetails(), context);
//        holder.recyclerViewBookingDetail.setAdapter(detailAdapter);

        // Handle button click listener
        holder.btnConfirm.setOnClickListener(v -> {
            // Handle Confirm button click
        });

        holder.btnCancel.setOnClickListener(v -> {
            // Handle Cancel button click
        });

        holder.infoIcon.setOnClickListener(v -> {
            // Handle Info Icon click (show more info, etc.)
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, email, bookingDate, status, totalAmount;
        Button btnConfirm, btnCancel;
        ImageView infoIcon;
        RecyclerView recyclerViewBookingDetail;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.booking_id);
            email = itemView.findViewById(R.id.tvEmailUser);
            bookingDate = itemView.findViewById(R.id.tvDateBooking);
            status = itemView.findViewById(R.id.tvStatus);
            totalAmount = itemView.findViewById(R.id.tvTotal);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            infoIcon = itemView.findViewById(R.id.infoIcon);
            recyclerViewBookingDetail = itemView.findViewById(R.id.recyclerViewBookingDetail);
        }
    }
}
