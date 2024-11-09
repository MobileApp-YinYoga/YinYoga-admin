package com.example.yinyoga.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "YogaAppAdminSQLite.db";  // Tên cơ sở dữ liệu
    private static final int DATABASE_VERSION = 1;

    // Bảng Courses
    private static final String TABLE_COURSES = "Courses";
    private static final String COLUMN_COURSE_ID = "CourseId";
    private static final String COLUMN_COURSE_NAME = "CourseName";
    private static final String COLUMN_DAY_OF_WEEK = "DayOfWeek";    // Ngày trong tuần
    private static final String COLUMN_TIME = "Time";                // Thời gian
    private static final String COLUMN_CAPACITY = "Capacity";        // Số lượng học viên tối đa
    private static final String COLUMN_DURATION = "Duration";        // Thời lượng của lớp học (phút)
    private static final String COLUMN_PRICE = "Price";              // Giá của lớp học
    private static final String COLUMN_COURSE_TYPE = "CourseType";   // Loại lớp học
    private static final String COLUMN_DESCRIPTION = "Description";  // Mô tả khóa học

    // Bảng Roles
    private static final String TABLE_ROLES = "Roles";
    private static final String COLUMN_ROLE_ID = "RoleId";          // Mã vai trò
    private static final String COLUMN_ROLE_NAME = "RoleName";      // Tên vai trò
    private static final String COLUMN_ROLE_DESCRIPTION = "Description"; // Mô tả vai trò

    // Bảng Users
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USERNAME = "Username";        // Tên người dùng
    private static final String COLUMN_FULL_NAME = "FullName";      // Họ và tên
    private static final String COLUMN_EMAIL = "Email";              // Địa chỉ email
    private static final String COLUMN_PASSWORD = "Password";        // Mật khẩu
    private static final String COLUMN_ROLE_ID_FK = "RoleId";       // Mã vai trò (liên kết tới bảng Roles)

    // Bảng ClassInstances
    private static final String TABLE_CLASS_INSTANCES = "ClassInstances";
    private static final String COLUMN_INSTANCE_ID = "InstanceId";       // Mã phiên học
    private static final String COLUMN_INSTANCE_COURSE_ID = "CourseId";           // Mã khóa học
    private static final String COLUMN_DATES = "Date";                  // Ngày diễn ra phiên học
    private static final String COLUMN_TEACHER = "Teacher";         // Giáo viên phụ trách

    // Bảng Notifications
    private static final String TABLE_NOTIFICATIONS = "Notifications";
    private static final String COLUMN_NOTIFICATION_ID = "NotificationId";
    private static final String COLUMN_NOTIFICATION_TITLE = "Title";
    private static final String COLUMN_NOTIFICATION_DESCRIPTION = "Description";
    private static final String COLUMN_NOTIFICATION_TIME = "Time";
        private static final String COLUMN_NOTIFICATION_IS_READ = "IsRead";
    private static final String COLUMN_NOTIFICATION_CREATED_DATE = "CreatedDate";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Tạo bảng Roles
            String CREATE_ROLES_TABLE = "CREATE TABLE " + TABLE_ROLES + " ("
                    + COLUMN_ROLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ROLE_NAME + " TEXT, "
                    + COLUMN_ROLE_DESCRIPTION + " TEXT)";
            db.execSQL(CREATE_ROLES_TABLE);
            Log.d("Database", "Table Roles created successfully");

            // Tạo bảng Users
            String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY , " +
                    COLUMN_FULL_NAME + " TEXT, " + // Thêm cột Họ và Tên
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE_ID_FK + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_ROLE_ID_FK + ") REFERENCES " + TABLE_ROLES + "(" + COLUMN_ROLE_ID + "))";
            db.execSQL(CREATE_USERS_TABLE);
            Log.d("Database", "Table Users created successfully");

            // Tạo bảng Courses
            String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + " ("
                    + COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_COURSE_NAME + " TEXT NOT NULL, "
                    + COLUMN_DAY_OF_WEEK + " TEXT NOT NULL, "  // Ngày trong tuần
                    + COLUMN_TIME + " TEXT NOT NULL, "          // Thời gian bắt đầu của khóa học
                    + COLUMN_CAPACITY + " INTEGER NOT NULL, "   // Sức chứa tối đa
                    + COLUMN_DURATION + " INTEGER NOT NULL, "   // Thời lượng khóa học
                    + COLUMN_PRICE + " REAL NOT NULL, "         // Giá mỗi buổi học
                    + COLUMN_COURSE_TYPE + " TEXT NOT NULL)";            // Mô tả khóa học (không bắt buộc)
            db.execSQL(CREATE_COURSES_TABLE);
            Log.d("Database", "Table Courses created successfully");

            // Tạo bảng ClassInstances
            String CREATE_CLASS_INSTANCES_TABLE = "CREATE TABLE " + TABLE_CLASS_INSTANCES + " ("
                    + COLUMN_INSTANCE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_INSTANCE_COURSE_ID + " INTEGER NOT NULL, "
                    + COLUMN_DATES + " TEXT NOT NULL, "
                    + COLUMN_TEACHER + " TEXT, "
                    + "FOREIGN KEY(" + COLUMN_INSTANCE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COLUMN_COURSE_ID + "))";

            db.execSQL(CREATE_CLASS_INSTANCES_TABLE);
            Log.d("Database", "Table Class Instances created successfully");

            // Tạo bảng Notifications
            String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + " ("
                    + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NOTIFICATION_TITLE + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_DESCRIPTION + " TEXT, "
                    + COLUMN_NOTIFICATION_TIME + " TEXT NOT NULL, "
                    + COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0, "
                    + COLUMN_NOTIFICATION_CREATED_DATE + " TEXT NOT NULL)";
            db.execSQL(CREATE_NOTIFICATIONS_TABLE);
            Log.d("Database", "Table Notifications created successfully");

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
            onCreate(db);  // Gọi lại hàm onCreate để tạo lại bảng khi cơ sở dữ liệu được nâng cấp
            Log.d("Database", "Database upgraded successfully");
        } catch (Exception e) {
            Log.e("Database", "Error upgrading database: " + e.getMessage());
        }
    }
}
