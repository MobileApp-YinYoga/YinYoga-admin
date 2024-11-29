package com.example.yinyoga.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.yinyoga.database.Database;
import com.example.yinyoga.models.BookingDetail;
import com.example.yinyoga.repository.BookingDetailRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncBookingDetailManager {
    private final FirebaseFirestore db;
    private final Database dbHelper;
    private final BookingDetailRepository bookingDetailRepository;

    public SyncBookingDetailManager(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.dbHelper = new Database(context);
        this.bookingDetailRepository = new BookingDetailRepository(context);
    }

    public void syncBookingDetailsToFirestore() {
        // Query your SQLite database to get the data
        List<BookingDetail> bookingDetailList = bookingDetailRepository.getAllBookingDetails();
        for (BookingDetail bookingDetail : bookingDetailList) {
            // Prepare the data for Firestore
            Map<String, Object> bookingDetailMap = new HashMap<>();
            bookingDetailMap.put("bookingId", bookingDetail.getBookingId());
            bookingDetailMap.put("instanceId", bookingDetail.getInstanceId());
            bookingDetailMap.put("price", bookingDetail.getPrice());

            // Add the data to Firestore
            db.collection("bookingDetails").document(bookingDetail.getBookingDetailId())
                    .set(bookingDetailMap)
                    .addOnSuccessListener(aVoid -> Log.d("SyncManager", "Document " + bookingDetail.getBookingDetailId() + " inserted into bookingDetails"))
                    .addOnFailureListener(e -> Log.w("SyncManager", "Error inserting document into bookingDetails", e));
        }
    }

    public void syncBookingDetailsFromFirestore() {
        // Fetch all booking details from Firestore
        db.collection("bookingDetails")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookingDetailId = document.getId();
                            String bookingId = document.getString("bookingId");
                            String instanceId = document.getString("instanceId");
                            double price = document.getDouble("price").doubleValue();

                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("bookingDetailId", bookingDetailId);
                            values.put("bookingId", bookingId);
                            values.put("instanceId", instanceId);
                            values.put("price", price);

                            // Check if the record already exists to decide insert/update
                            int rowsAffected = db.update("bookingDetails", values, "bookingDetailId = ?", new String[]{bookingDetailId});
                            if (rowsAffected == 0) {
                                db.insert("bookingDetails", null, values);
                            }

                            Log.d("SyncManager", "All documents in 'bookingDetails' collection inserted into SQLite");
                        }
                    } else {
                        Log.w("SyncManager", "Error fetching documents from bookingDetails for insertion into SQLite.", task.getException());
                    }
                });
    }
}
