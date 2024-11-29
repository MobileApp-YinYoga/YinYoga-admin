package com.example.yinyoga.models;

public class BookingDetail {
    private String bookingDetailId;
    private String bookingId;
    private String instanceId;
    private double price;

    public BookingDetail(String bookingDetailId, String bookingId, String instanceId, double price) {
        this.bookingDetailId = bookingDetailId;
        this.bookingId = bookingId;
        this.instanceId = instanceId;
        this.price = price;
    }

    public String getBookingDetailId() {
        return bookingDetailId;
    }

    public void setBookingDetailId(String bookingDetailId) {
        this.bookingDetailId = bookingDetailId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
