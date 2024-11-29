package com.example.yinyoga.models;

public class Booking {
    private String bookingId;
    private String email;
    private String bookingDate;
    private String status;
    private double totalAmount;

    // Constructor
    public Booking(String bookingId, String email, String bookingDate, String status, double totalAmount) {
        this.bookingId = bookingId;
        this.email = email;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
