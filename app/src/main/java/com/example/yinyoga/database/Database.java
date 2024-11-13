package com.example.yinyoga.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "YogaAppAdminSQLite.db";  // Tên cơ sở dữ liệu
    private static final int DATABASE_VERSION = 1;

    // Bảng courses
    private static final String TABLE_COURSES = "courses";
    private static final String COLUMN_COURSE_ID = "courseId";
    private static final String COLUMN_COURSE_NAME = "courseName";
    private static final String COLUMN_COURSE_TYPE = "courseType";
    private static final String COLUMN_CREATED_AT = "createdAt";
    private static final String COLUMN_DAY_OF_WEEK = "dayOfWeek";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_IMAGE_URL = "imageUrl";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_TIME = "time";

    // Bảng roles
    private static final String TABLE_ROLES = "roles";
    private static final String COLUMN_ROLE_ID = "roleId";          // Mã vai trò
    private static final String COLUMN_ROLE_NAME = "roleName";      // Tên vai trò
    private static final String COLUMN_ROLE_DESCRIPTION = "description"; // Mô tả vai trò

    // Bảng users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";        // Tên người dùng
    private static final String COLUMN_FULL_NAME = "fullName";      // Họ và tên
    private static final String COLUMN_EMAIL = "email";              // Địa chỉ email
    private static final String COLUMN_PASSWORD = "password";        // Mật khẩu
    private static final String COLUMN_ROLE_ID_FK = "roleId";       // Mã vai trò (liên kết tới bảng roles)

    // Bảng classInstances
    private static final String TABLE_CLASS_INSTANCES = "classInstances";
    private static final String COLUMN_INSTANCE_ID = "instanceId";       // Mã phiên học
    private static final String COLUMN_INSTANCE_COURSE_ID = "courseId";           // Mã khóa học
    private static final String COLUMN_DATES = "date";                  // Ngày diễn ra phiên học
    private static final String COLUMN_TEACHER = "teacher";         // Giáo viên phụ trách
    private static final String COLUMN_INSTANCE_IMAGE_URL = "imageUrl";

    // Bảng notifications
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COLUMN_NOTIFICATION_ID = "notificationId";
    private static final String COLUMN_NOTIFICATION_EMAIL = "email";
    private static final String COLUMN_NOTIFICATION_TITLE = "title";
    private static final String COLUMN_NOTIFICATION_DESCRIPTION = "description";
    private static final String COLUMN_NOTIFICATION_TIME = "time";
    private static final String COLUMN_NOTIFICATION_IS_READ = "isRead";
    private static final String COLUMN_NOTIFICATION_CREATED_DATE = "createdDate";

    // Bảng Cart
    private static final String TABLE_CARTS = "carts";
    private static final String COLUMN_CART_ID = "cartId";
    private static final String COLUMN_CART_CREATED_DATE = "createdDate";
    private static final String COLUMN_CART_EMAIL = "email";
    private static final String COLUMN_CART_INSTANCE_ID = "instanceId";

    // Bảng Booking
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COLUMN_BOOKING_ID = "bookingId";
    private static final String COLUMN_BOOKING_DATE = "bookingDate";
    private static final String COLUMN_BOOKING_EMAIL = "email";
    private static final String COLUMN_BOOKING_STATUS = "status";
    private static final String COLUMN_BOOKING_TOTAL_AMOUNT = "totalAmount";

    // Bảng BookingDetails
    private static final String TABLE_BOOKING_DETAILS = "bookingDetails";
    private static final String COLUMN_BOOKING_DETAIL_ID = "bookingDetailId";
    private static final String COLUMN_BOOKING_DETAIL_BOOKING_ID = "bookingId";
    private static final String COLUMN_BOOKING_DETAIL_INSTANCE_ID = "instanceId";
    private static final String COLUMN_BOOKING_DETAIL_PRICE = "price";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Tạo bảng roles
            String CREATE_ROLES_TABLE = "CREATE TABLE " + TABLE_ROLES + " ("
                    + COLUMN_ROLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ROLE_NAME + " TEXT, "
                    + COLUMN_ROLE_DESCRIPTION + " TEXT)";
            db.execSQL(CREATE_ROLES_TABLE);
            Log.d("Database", "Table roles created successfully");

            // Tạo bảng users
            String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY , " +
                    COLUMN_FULL_NAME + " TEXT, " + // Thêm cột Họ và Tên
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE_ID_FK + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_ROLE_ID_FK + ") REFERENCES " + TABLE_ROLES + "(" + COLUMN_ROLE_ID + "))";
            db.execSQL(CREATE_USERS_TABLE);
            Log.d("Database", "Table users created successfully");

            // Tạo bảng courses
            String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + " ("
                    + COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_COURSE_NAME + " TEXT NOT NULL, "
                    + COLUMN_COURSE_TYPE + " TEXT NOT NULL, "
                    + COLUMN_CREATED_AT + " TEXT, "
                    + COLUMN_DAY_OF_WEEK + " TEXT, "
                    + COLUMN_DESCRIPTION + " TEXT, "
                    + COLUMN_CAPACITY + " INTEGER NOT NULL, "
                    + COLUMN_DURATION + " INTEGER NOT NULL, "
                    + COLUMN_IMAGE_URL + " TEXT, "
                    + COLUMN_PRICE + " REAL NOT NULL, "
                    + COLUMN_TIME + " TEXT)";
            db.execSQL(CREATE_COURSES_TABLE);
            Log.d("Database", "Table courses created successfully");

            // Tạo bảng classInstances
            String CREATE_CLASS_INSTANCES_TABLE = "CREATE TABLE " + TABLE_CLASS_INSTANCES + " ("
                    + COLUMN_INSTANCE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_INSTANCE_COURSE_ID + " INTEGER NOT NULL, "
                    + COLUMN_DATES + " TEXT NOT NULL, "
                    + COLUMN_TEACHER + " TEXT, "
                    + COLUMN_INSTANCE_IMAGE_URL + " TEXT, "
                    + "FOREIGN KEY(" + COLUMN_INSTANCE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COLUMN_COURSE_ID + "))";

            db.execSQL(CREATE_CLASS_INSTANCES_TABLE);
            Log.d("Database", "Table Class Instances created successfully");

            // Tạo bảng notifications
            String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + " ("
                    + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NOTIFICATION_EMAIL + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_TITLE + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_DESCRIPTION + " TEXT, "
                    + COLUMN_NOTIFICATION_TIME + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0, "
                    + COLUMN_NOTIFICATION_CREATED_DATE + " TEXT NOT NULL)";
            db.execSQL(CREATE_NOTIFICATIONS_TABLE);
            Log.d("Database", "Table notifications created successfully");

            // Tạo bảng Cart
            String CREATE_CARTS_TABLE = "CREATE TABLE " + TABLE_CARTS + " ("
                    + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_CART_CREATED_DATE + " TEXT NOT NULL, "
                    + COLUMN_CART_EMAIL + " TEXT NOT NULL, "
                    + COLUMN_CART_INSTANCE_ID + " TEXT NOT NULL)";
            db.execSQL(CREATE_CARTS_TABLE);
            Log.d("Database", "Table arts created successfully");

            // Tạo bảng Booking
            String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + " ("
                    + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_BOOKING_DATE + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_EMAIL + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_STATUS + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_TOTAL_AMOUNT + " REAL NOT NULL)";
            db.execSQL(CREATE_BOOKINGS_TABLE);
            Log.d("Database", "Table Bookings created successfully");

            // Tạo bảng BookingDetails
            String CREATE_BOOKING_DETAILS_TABLE = "CREATE TABLE " + TABLE_BOOKING_DETAILS + " ("
                    + COLUMN_BOOKING_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_BOOKING_DETAIL_BOOKING_ID + " INTEGER NOT NULL, "
                    + COLUMN_BOOKING_DETAIL_INSTANCE_ID + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_DETAIL_PRICE + " REAL NOT NULL, "
                    + "FOREIGN KEY(" + COLUMN_BOOKING_DETAIL_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(" + COLUMN_BOOKING_ID + "))";
            db.execSQL(CREATE_BOOKING_DETAILS_TABLE);

        } catch (Exception e) {
            Log.e("Database", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING_DETAILS);
            onCreate(db);  // Gọi lại hàm onCreate để tạo lại bảng khi cơ sở dữ liệu được nâng cấp
            Log.d("Database", "Database upgraded successfully");
        } catch (Exception e) {
            Log.e("Database", "Error upgrading database: " + e.getMessage());
        }
    }
}
