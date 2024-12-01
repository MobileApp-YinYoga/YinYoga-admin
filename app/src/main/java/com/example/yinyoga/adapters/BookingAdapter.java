package com.example.yinyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.models.Booking;
import com.example.yinyoga.models.BookingDetail;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.service.BookingDetailService;
import com.example.yinyoga.service.ClassInstanceService;
import com.example.yinyoga.service.CourseService;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    public interface CustomListeners {
        void confirmBooking(Booking booking);
        void cancelBooking(Booking booking);
    }

    private List<Booking> bookingList;
    private Context context;
    private BookingDetailService bookingDetailService;
    private ClassInstanceService classInstanceService;
    private CourseService courseService;
    private CustomListeners customListeners;

    public BookingAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.bookingDetailService = new BookingDetailService(context);
        this.classInstanceService = new ClassInstanceService(context);
        this.courseService = new CourseService(context);
    }

    public void setCustomListeners(CustomListeners customListeners) {
        this.customListeners = customListeners;
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

        List<ClassInstance> detailList = getDetailListForBooking(booking.getBookingId());
        DetailAdapter detailAdapter = new DetailAdapter(detailList, context);
        holder.recyclerViewBookingDetail.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewBookingDetail.setAdapter(detailAdapter);

        holder.btnConfirm.setEnabled(true);
        holder.btnCancel.setEnabled(true);
        holder.btnConfirm.setVisibility(View.VISIBLE);
        holder.btnCancel.setVisibility(View.VISIBLE);

        if (booking.getStatus().equalsIgnoreCase("confirmed")) {
            holder.btnConfirm.setEnabled(false);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnConfirm.setText("Confirmed");
        } else if (booking.getStatus().equals("cancelled")) {
            holder.btnCancel.setEnabled(false);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setText("Cancelled");
        }

        // Handle button click listener
        holder.btnConfirm.setOnClickListener(v -> {
            // Handle Confirm button click
            customListeners.confirmBooking(booking);
            holder.btnConfirm.setEnabled(false);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnConfirm.setText("Confirmed");
        });

        holder.btnCancel.setOnClickListener(v -> {
            // Handle Cancel button click
            customListeners.cancelBooking(booking);
            holder.btnCancel.setEnabled(false);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setText("Cancelled");
        });

        holder.infoIcon.setOnClickListener(v -> {
            // Handle Info Icon click (show more info, etc.)
            if (holder.recyclerViewBookingDetail.getVisibility() == View.VISIBLE) {
                holder.recyclerViewBookingDetail.setVisibility(View.GONE);
                holder.tvBottom.setVisibility(View.VISIBLE);
            } else {
                holder.recyclerViewBookingDetail.setVisibility(View.VISIBLE);
                holder.tvBottom.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, email, bookingDate, status, totalAmount, tvBottom;
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
            tvBottom = itemView.findViewById(R.id.tv_bottom);
        }
    }

    public void updateBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
        notifyDataSetChanged();
    }

    private List<ClassInstance> getDetailListForBooking(String bookingId) {
        // Replace this with actual logic to fetch detail data for the booking
        List<BookingDetail> bookingDetailList = bookingDetailService.getBookingDetails(bookingId);
        List<ClassInstance> detailList = new ArrayList<>();

        for (BookingDetail bookingDetail : bookingDetailList) {
            ClassInstance classInstance = classInstanceService.getClassInstance(bookingDetail.getInstanceId());
            Course course = courseService.getCourse(classInstance.getCourse().getCourseId());
            classInstance.setCourse(course);
            detailList.add(classInstance);
        }

        return detailList;
    }
}
