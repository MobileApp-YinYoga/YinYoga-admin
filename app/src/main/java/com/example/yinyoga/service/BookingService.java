package com.example.yinyoga.service;

import android.content.Context;

import com.example.yinyoga.models.Booking;
import com.example.yinyoga.repository.BookingRepository;

import java.util.List;

public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(Context context) {
        this.bookingRepository = new BookingRepository(context);
    }

    public void addBooking(Booking booking) {
        bookingRepository.insertBooking(booking);
    }

    public Booking getBooking(String bookingId) {
        return bookingRepository.getBooking(bookingId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.getAllBookings();
    }

    public List<Booking> getConfirmedBookings() {
        return bookingRepository.getConfirmedBookings();
    }

    public void confirmBooking(String bookingId) {
        bookingRepository.confirmBooking(bookingId);
    }

    public List<Booking> getCancelledBookings() {
        return bookingRepository.getCancelledBookings();
    }

    public void cancelBooking(String bookingId) {
        bookingRepository.cancelBooking(bookingId);
    }
}
