package com.example.yinyoga.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "YogaAppAdminSQLite.db";  // Tên cơ sở dữ liệu
    private static final int DATABASE_VERSION = 1;

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

    private static final String TABLE_ROLES = "roles";
    private static final String COLUMN_ROLE_ID = "roleId";
    private static final String COLUMN_ROLE_NAME = "roleName";
    private static final String COLUMN_ROLE_DESCRIPTION = "description";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_FULL_NAME = "fullName";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE_ID_FK = "roleId";

    private static final String TABLE_CLASS_INSTANCES = "classInstances";
    private static final String COLUMN_INSTANCE_ID = "instanceId";
    private static final String COLUMN_INSTANCE_COURSE_ID = "courseId";
    private static final String COLUMN_DATES = "date";
    private static final String COLUMN_TEACHER = "teacher";
    private static final String COLUMN_INSTANCE_IMAGE_URL = "imageUrl";

    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COLUMN_NOTIFICATION_ID = "notificationId";
    private static final String COLUMN_NOTIFICATION_EMAIL = "email";
    private static final String COLUMN_NOTIFICATION_TITLE = "title";
    private static final String COLUMN_NOTIFICATION_DESCRIPTION = "description";
    private static final String COLUMN_NOTIFICATION_TIME = "time";
    private static final String COLUMN_NOTIFICATION_IS_READ = "isRead";
    private static final String COLUMN_NOTIFICATION_CREATED_DATE = "createdDate";

    private static final String TABLE_CARTS = "carts";
    private static final String COLUMN_CART_ID = "cartId";
    private static final String COLUMN_CART_CREATED_DATE = "createdDate";
    private static final String COLUMN_CART_EMAIL = "email";
    private static final String COLUMN_CART_INSTANCE_ID = "instanceId";

    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COLUMN_BOOKING_ID = "bookingId";
    private static final String COLUMN_BOOKING_DATE = "bookingDate";
    private static final String COLUMN_BOOKING_EMAIL = "email";
    private static final String COLUMN_BOOKING_STATUS = "status";
    private static final String COLUMN_BOOKING_TOTAL_AMOUNT = "totalAmount";

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
            String CREATE_COURSES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + " ("
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

            String CREATE_CLASS_INSTANCES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CLASS_INSTANCES + " ("
                    + COLUMN_INSTANCE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_INSTANCE_COURSE_ID + " INTEGER NOT NULL, "
                    + COLUMN_DATES + " TEXT NOT NULL, "
                    + COLUMN_TEACHER + " TEXT, "
                    + COLUMN_INSTANCE_IMAGE_URL + " TEXT, "
                    + "FOREIGN KEY(" + COLUMN_INSTANCE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COLUMN_COURSE_ID + "))";

            db.execSQL(CREATE_CLASS_INSTANCES_TABLE);
            Log.d("Database", "Table Class Instances created successfully");

            String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " ("
                    + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NOTIFICATION_EMAIL + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_TITLE + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_DESCRIPTION + " TEXT, "
                    + COLUMN_NOTIFICATION_TIME + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0, "
                    + COLUMN_NOTIFICATION_CREATED_DATE + " TEXT NOT NULL)";
            db.execSQL(CREATE_NOTIFICATIONS_TABLE);
            Log.d("Database", "Table notifications created successfully");

            String CREATE_CARTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CARTS + " ("
                    + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_CART_CREATED_DATE + " TEXT NOT NULL, "
                    + COLUMN_CART_EMAIL + " TEXT NOT NULL, "
                    + COLUMN_CART_INSTANCE_ID + " TEXT NOT NULL)";
            db.execSQL(CREATE_CARTS_TABLE);
            Log.d("Database", "Table arts created successfully");

            String CREATE_BOOKINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + " ("
                    + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_BOOKING_DATE + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_EMAIL + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_STATUS + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_TOTAL_AMOUNT + " REAL NOT NULL)";
            db.execSQL(CREATE_BOOKINGS_TABLE);
            Log.d("Database", "Table Bookings created successfully");

            String CREATE_BOOKING_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKING_DETAILS + " ("
                    + COLUMN_BOOKING_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_BOOKING_DETAIL_BOOKING_ID + " INTEGER NOT NULL, "
                    + COLUMN_BOOKING_DETAIL_INSTANCE_ID + " TEXT NOT NULL, "
                    + COLUMN_BOOKING_DETAIL_PRICE + " REAL NOT NULL, "
                    + "FOREIGN KEY(" + COLUMN_BOOKING_DETAIL_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(" + COLUMN_BOOKING_ID + "))";
            db.execSQL(CREATE_BOOKING_DETAILS_TABLE);

            String CREATE_ROLES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROLES + " ("
                    + COLUMN_ROLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ROLE_NAME + " TEXT, "
                    + COLUMN_ROLE_DESCRIPTION + " TEXT)";
            db.execSQL(CREATE_ROLES_TABLE);
            Log.d("Database", "Table roles created successfully");

            String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY , " +
                    COLUMN_FULL_NAME + " TEXT, " + // Thêm cột Họ và Tên
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE_ID_FK + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_ROLE_ID_FK + ") REFERENCES " + TABLE_ROLES + "(" + COLUMN_ROLE_ID + "))";
            db.execSQL(CREATE_USERS_TABLE);
            Log.d("Database", "Table users created successfully");

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
            onCreate(db);
            Log.d("Database", "Database upgraded successfully");
        } catch (Exception e) {
            Log.e("Database", "Error upgrading database: " + e.getMessage());
        }
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);

            onCreate(db);

            Log.d("Database", "Database reset successfully (except Users table)");
        } catch (Exception e) {
            Log.e("Database", "Error resetting database: " + e.getMessage());
        }
    }

}
