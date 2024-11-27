package com.example.yinyoga.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatetimeHelper {
    public static String getCurrentDatetime() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(currentDate);
    }
    public static boolean isToday(String createdDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(createdDate);

            // Lấy ngày hiện tại
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return date != null && dateFormat.format(date).equals(dateFormat.format(today.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String formatDateWithSuffix(Date date) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

        String day = dayFormat.format(date);
        String monthYear = monthYearFormat.format(date);

        String dayWithSuffix = day + getDaySuffix(Integer.parseInt(day));

        return monthYear.replace(",", ", " + dayWithSuffix);
    }

    public static String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String getDayOfWeekName(int dayOfWeek) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        if (dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.SATURDAY) {
            return days[dayOfWeek];
        } else {
            return "Invalid day";
        }
    }
}
