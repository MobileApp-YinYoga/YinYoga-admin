package com.example.yinyoga.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.adapters.BookingAdapter;
import com.example.yinyoga.adapters.CourseAdapter;
import com.example.yinyoga.adapters.DetailAdapter;
import com.example.yinyoga.models.Booking;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.service.BookingService;
import com.example.yinyoga.service.ClassInstanceService;
import com.example.yinyoga.service.CourseService;
import com.example.yinyoga.sync.SyncBookingDetailManager;
import com.example.yinyoga.sync.SyncBookingManager;
import com.example.yinyoga.sync.SyncCourseManager;
import com.example.yinyoga.utils.DialogHelper;

import java.util.ArrayList;
import java.util.List;

public class ManageBookingFragment extends Fragment implements BookingAdapter.CustomListeners {
    private RecyclerView manageBookingRecyclerView;
    private BookingAdapter bookingAdapter;
    private LinearLayout llAllBookings, llConfirmedBookings, llCancelledBookings;
    private List<Booking> bookingList;
    private BookingService bookingService;
    private SyncBookingManager syncBookingManager;
    private SyncBookingDetailManager syncBookingDetailManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadCourseFromDatabase();

        llAllBookings.setOnClickListener(v -> {
            bookingList.clear();
            bookingList = bookingService.getAllBookings();
            bookingAdapter.updateBookingList(bookingList);
        });

        llConfirmedBookings.setOnClickListener(v -> {
            bookingList.clear();
            bookingList = bookingService.getConfirmedBookings();
            bookingAdapter.updateBookingList(bookingList);
        });

        llCancelledBookings.setOnClickListener(v -> {
            bookingList.clear();
            bookingList = bookingService.getCancelledBookings();
            bookingAdapter.updateBookingList(bookingList);
        });
    }

    public void loadCourseFromDatabase() {
        syncBookingManager.syncBookingsToFirestore();
        syncBookingManager.syncBookingsFromFirestore();

        syncBookingDetailManager.syncBookingDetailsFromFirestore();
        syncBookingDetailManager.syncBookingDetailsFromFirestore();

        bookingList.clear();
        bookingList = bookingService.getAllBookings();

        if (bookingList != null) {
            bookingAdapter.updateBookingList(bookingList);
        }
    }

    private void setupRecyclerView() {
        manageBookingRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        bookingList = new ArrayList<>();
        bookingService = new BookingService(requireContext());
        bookingAdapter = new BookingAdapter(bookingList, requireContext());

        bookingAdapter.setCustomListeners(this);
        manageBookingRecyclerView.setAdapter(bookingAdapter);
    }

    private void initViews(View view) {
        manageBookingRecyclerView = view.findViewById(R.id.recyclerViewManageBooking);
        syncBookingManager = new SyncBookingManager(requireContext());
        syncBookingDetailManager = new SyncBookingDetailManager(requireContext());

        llAllBookings = view.findViewById(R.id.linear_all_booking);
        llConfirmedBookings = view.findViewById(R.id.linear_approve);
        llCancelledBookings = view.findViewById(R.id.linear_reject);
    }

    @Override
    public void confirmBooking(Booking booking) {
        DialogHelper.showConfirmationDialog(
                requireActivity(),
                "Are you sure you want to confirm this booking?",
                null,
                null,
                () -> {
                    bookingService.confirmBooking(booking.getBookingId());
                    loadCourseFromDatabase();
                    DialogHelper.showSuccessDialog(requireActivity(), "Booking confirmed successfully!");
                });
    }

    @Override
    public void cancelBooking(Booking booking) {
        DialogHelper.showConfirmationDialog(
                requireActivity(),
                "Are you sure you want to cancel this booking?",
                null,
                null,
                () -> {
                    bookingService.cancelBooking(booking.getBookingId());
                    loadCourseFromDatabase();
                    DialogHelper.showSuccessDialog(requireActivity(), "Booking cancelled successfully!");
                });
    }
}
