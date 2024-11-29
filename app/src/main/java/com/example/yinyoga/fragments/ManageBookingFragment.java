package com.example.yinyoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.adapters.BookingAdapter;
import com.example.yinyoga.adapters.DetailAdapter;
import com.example.yinyoga.models.Booking;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.models.Course;

import java.util.ArrayList;
import java.util.List;

public class ManageBookingFragment extends Fragment {
    private RecyclerView bookingRecyclerView;
    private RecyclerView detailRecyclerView;
    private BookingAdapter bookingAdapter;
    private DetailAdapter detailAdapter;
    private List<Booking> bookingList;
    private List<ClassInstance> detailList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the RecyclerView
        bookingRecyclerView = view.findViewById(R.id.recyclerViewManageBooking);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the booking list and add some sample data
        bookingList = new ArrayList<>();
        bookingList.add(new Booking("YUxjdhdwfjsjn54547", "user@example.com", "2024-11-29", "Confirmed", "$50"));
        bookingList.add(new Booking("Aixjdhdwfjsjn12345", "anotheruser@example.com", "2024-11-28", "Pending", "$40"));

        // Set the adapter for RecyclerView
        bookingAdapter = new BookingAdapter(bookingList, getContext());
        bookingRecyclerView.setAdapter(bookingAdapter);

        // Initialize the booking detail list with sample data
        detailList = new ArrayList<>();
        Course sampleCourse = new Course(1, "Learn the basics of yoga");
        detailList.add(new ClassInstance("Class001", sampleCourse, "2024-11-29", "John Doe", new byte[0]));  // Example with empty image data
        detailList.add(new ClassInstance("Class002", sampleCourse, "2024-12-01", "Jane Smith", new byte[0]));  // Example with empty image data

        // Initialize the booking detail RecyclerView
        detailRecyclerView = view.findViewById(R.id.recyclerViewBookingDetail);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set the adapter for booking detail RecyclerView
        detailAdapter = new DetailAdapter(detailList, this.getContext());
        detailRecyclerView.setAdapter(detailAdapter);
    }
}
