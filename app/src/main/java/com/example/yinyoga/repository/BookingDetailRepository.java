package com.example.yinyoga.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.BookingDetail;

import java.util.ArrayList;
import java.util.List;

public class BookingDetailRepository {
    private final Database database;

    public BookingDetailRepository(Context context) {
        this.database = new Database(context);
    }

    public List<BookingDetail> getAllBookingDetails() {
        SQLiteDatabase db = this.database.getReadableDatabase();
        List<BookingDetail> bookingDetails = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM bookingDetails", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bookingDetailId = cursor.getString(0);
                String bookingId = cursor.getString(1);
                String instanceId = cursor.getString(2);
                double price = cursor.getDouble(3);

                BookingDetail bookingDetail = new BookingDetail(bookingDetailId, bookingId, instanceId, price);
                bookingDetails.add(bookingDetail);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingDetails;
    }

    public List<BookingDetail> getBookingDetails(String bookingId) {
        SQLiteDatabase db = this.database.getReadableDatabase();
        List<BookingDetail> bookingDetails = new ArrayList<>();
        String query = "SELECT * FROM bookingDetails WHERE bookingId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{bookingId});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bookingDetailId = cursor.getString(0);
                String instanceId = cursor.getString(2);
                double price = cursor.getDouble(3);

                BookingDetail bookingDetail = new BookingDetail(bookingDetailId, bookingId, instanceId, price);
                bookingDetails.add(bookingDetail);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingDetails;
    }
}
