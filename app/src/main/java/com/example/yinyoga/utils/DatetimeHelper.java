package com.example.yinyoga.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatetimeHelper {
    public static String getCurrentDatetime() {
        Date currentDate = Calendar.getInstance().getTime(); // Lấy ngày hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(currentDate); // Convert Date to String
        return formattedDate;
    }
}
