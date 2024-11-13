package com.example.yinyoga.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class ImageHelper {
    public static byte[] convertDrawableToByteArray(Context context, int drawableId) {
        // Step 1: Decode the drawable resource into a Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);

        // Step 2: Create a ByteArrayOutputStream to hold the bitmap data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Step 3: Compress the bitmap to PNG format and write to output stream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        // Step 4: Convert ByteArrayOutputStream to byte array
        byte[] byteArray = outputStream.toByteArray();

        // Optional: Recycle the bitmap to free memory if no longer needed
        bitmap.recycle();

        return byteArray;
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArr) {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
    }

    public static byte[] getImageBytes(ImageView imgTaskImage) {
        // Get the BitmapDrawable object from the ImageView object
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imgTaskImage.getDrawable();
        // Get the Bitmap object from the BitmapDrawable object
        Bitmap bitmap = bitmapDrawable.getBitmap();
        // Convert the Bitmap object to a byte array
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);

        return byteArray.toByteArray();
    }

    public static byte[] compressImage(byte[] imageData) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        // Resize the image (e.g., to 200x200 pixels)
        int targetWidth = 434; // adjust based on required size
        int targetHeight = targetWidth * 9 / 18;

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

        // Compress to JPEG format with 50% quality to further reduce the size
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

        return outputStream.toByteArray();
    }
}
