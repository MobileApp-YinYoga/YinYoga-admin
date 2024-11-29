package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private final Database database;

    public BookingRepository(Context context) {
        this.database = new Database(context);
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bookings", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bookingId = cursor.getString(0);
                String bookingDate = cursor.getString(1);
                String email = cursor.getString(2);
                String status = cursor.getString(3);
                double totalAmount = cursor.getDouble(4);

                Booking booking = new Booking(bookingId, email, bookingDate, status, totalAmount);
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingList;
    }

    public Booking getBooking(String bookingId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Booking booking = null;

        Cursor cursor = db.rawQuery("SELECT * FROM bookings WHERE bookingId = '" + bookingId + "'", null);

        if (cursor != null && cursor.moveToFirst()) {
            String bookingDate = cursor.getString(1);
            String email = cursor.getString(2);
            String status = cursor.getString(3);
            double totalAmount = cursor.getDouble(4);

            booking = new Booking(bookingId, email, bookingDate, status, totalAmount);
        }

        cursor.close();
        return booking;
    }

    public void insertBooking(Booking booking) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "INSERT INTO bookings (bookingId, email, bookingDate, status, totalAmount) VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, booking.getBookingId());
        statement.bindString(2, booking.getBookingDate());
        statement.bindString(3, booking.getEmail());
        statement.bindString(4, booking.getStatus());
        statement.bindDouble(5, booking.getTotalAmount());
        statement.executeInsert();
    }

    public List<Booking> getConfirmedBookings() {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bookings WHERE status = 'confirmed'", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bookingId = cursor.getString(0);
                String bookingDate = cursor.getString(1);
                String email = cursor.getString(2);
                String status = cursor.getString(3);
                double totalAmount = cursor.getDouble(4);

                Booking booking = new Booking(bookingId, email, bookingDate, status, totalAmount);
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingList;
    }

    public void confirmBooking(String bookingId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE bookings SET status = 'confirmed' WHERE bookingId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, bookingId);
        statement.executeUpdateDelete();
    }

    public List<Booking> getCancelledBookings() {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bookings WHERE status = 'cancelled'", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bookingId = cursor.getString(0);
                String bookingDate = cursor.getString(1);
                String email = cursor.getString(2);
                String status = cursor.getString(3);
                double totalAmount = cursor.getDouble(4);

                Booking booking = new Booking(bookingId, email, bookingDate, status, totalAmount);
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingList;
    }

    public void cancelBooking(String bookingId) {
        SQLiteDatabase db = database.getWritableDatabase();
        String query = "UPDATE bookings SET status = 'cancelled' WHERE bookingId = ?";
        SQLiteStatement statement = db.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, bookingId);
        statement.executeUpdateDelete();
    }
}
