package com.example.yinyoga.service;

import android.content.Context;

import com.example.yinyoga.models.BookingDetail;
import com.example.yinyoga.repository.BookingDetailRepository;

import java.util.List;

public class BookingDetailService {
    private final BookingDetailRepository bookingDetailRepository;

    public BookingDetailService(Context context) {
        this.bookingDetailRepository = new BookingDetailRepository(context);
    }

    public List<BookingDetail> getAllBookingDetails() {
        return bookingDetailRepository.getAllBookingDetails();
    }

    public List<BookingDetail> getBookingDetails(String bookingId) {
        return bookingDetailRepository.getBookingDetails(bookingId);
    }
}
