package com.example.yinyoga.models;

public class Booking {
    private String bookingId;
    private String email;
    private String bookingDate;
    private String status;
    private String totalAmount;

    // Constructor
    public Booking(String bookingId, String email, String bookingDate, String status, String totalAmount) {
        this.bookingId = bookingId;
        this.email = email;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public String getEmail() { return email; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    public String getTotalAmount() { return totalAmount; }
}
