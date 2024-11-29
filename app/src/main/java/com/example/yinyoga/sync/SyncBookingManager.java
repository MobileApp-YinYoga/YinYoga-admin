package com.example.yinyoga.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.Booking;
import com.example.yinyoga.repository.BookingRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncBookingManager {
    private final FirebaseFirestore db;
    private final Database dbHelper;
    private final BookingRepository bookingRepository;

    public SyncBookingManager(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.dbHelper = new Database(context);
        this.bookingRepository = new BookingRepository(context);
    }

    public void syncBookingsToFirestore() {
        // Query your SQLite database to get the data
        List<Booking> bookingList = bookingRepository.getAllBookings();

        for (Booking booking : bookingList) {
            // Prepare the data for Firestore
            Map<String, Object> bookingMap = new HashMap<>();
            bookingMap.put("email", booking.getEmail());
            bookingMap.put("bookingDate", booking.getBookingDate());
            bookingMap.put("status", booking.getStatus());
            bookingMap.put("totalAmount", booking.getTotalAmount());

            // Add the data to Firestore
            db.collection("bookings").document(booking.getBookingId())
                    .set(bookingMap)
                    .addOnSuccessListener(aVoid -> Log.d("SyncManager", "Document " + booking.getBookingId() + " inserted into bookings"))
                    .addOnFailureListener(e -> Log.w("SyncManager", "Error inserting document into bookings", e));
        }
    }

    public void syncBookingsFromFirestore() {
        // Fetch all bookings from Firestore
        db.collection("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookingId = document.getId();
                            String email = document.getString("email");
                            String bookingDate = document.getString("bookingDate");
                            String status = document.getString("status");
                            double totalAmount = document.getDouble("totalAmount").doubleValue();

                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("bookingId", bookingId);
                            values.put("email", email);
                            values.put("bookingDate", bookingDate);
                            values.put("status", status);
                            values.put("totalAmount", totalAmount);

                            // Check if the record already exists to decide insert/update
                            int rowsAffected = db.update("bookings", values, "bookingId = ?", new String[]{bookingId});
                            if (rowsAffected == 0) {
                                db.insert("bookings", null, values);
                            }

                            Log.d("SyncManager", "Document " + bookingId + " inserted/updated in bookings");
                        }
                    } else {
                        Log.w("SyncManager", "Error getting documents.", task.getException());
                    }
                });
    }
}
