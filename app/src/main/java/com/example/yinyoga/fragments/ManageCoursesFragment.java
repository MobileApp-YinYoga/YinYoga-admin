package com.example.yinyoga.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yinyoga.R;
import com.example.yinyoga.adapters.CourseAdapter;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.service.CourseService;
import com.example.yinyoga.utils.DatetimeHelper;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.ImageHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ManageCoursesFragment extends Fragment {
    private Dialog dialog;
    private RecyclerView recyclerView;
    private CourseAdapter coursesAdapter;
    private List<Course> courseLists;
    private LinearLayout add_task;
    private Spinner spinnerDayOfTheWeek, courseTypeSpinner;
    private EditText edCourseName, edTime, edDuration, edCapacity, edPrice, edDescription;
    private EditText searchInput;
    private TextView tvCourseId, tvTitle, tvSubtitle, tvClearSearch;
    private CourseService courseService;
    private ImageView imgGallery;
    private byte[] imageBytes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout của màn hình "Manage Courses"
        return inflater.inflate(R.layout.fragment_manage_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DialogHelper.showLoadingDialog(this.getContext(), "Loading all courses...");

        initViews(view);
        setupRecyclerView();
        loadCourseFromDatabase();

        // Xử lý sự kiện search
        setEventTextChangeForSearch();

        // Xử lý sự kiện thêm lớp học mới
        add_task.setOnClickListener(v -> openAddClassPopup(-1));
    }

    // Mở popup để thêm hoặc cập nhật lớp học
    public void openAddClassPopup(int courseId) {
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_rounded_border);

        LinearLayout btnClosePopup = dialog.findViewById(R.id.btnClose);
        Button btnSave = dialog.findViewById(R.id.btnSaveCourse);
        Button btnClearAllPopup = dialog.findViewById(R.id.btnClearAll);
        Button btnUploadImage = dialog.findViewById(R.id.btnUploadImage);

        btnUploadImage.setOnClickListener(this::chooseImage);

        setPopupEventListeners(btnClosePopup, btnClearAllPopup);
        setPopupAddOrUpdate(courseId);

        btnSave.setOnClickListener(v -> saveCourse(courseId));

        dialog.show();
    }

    private final ActivityResultLauncher<Intent> chooseImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageFileBath = data.getData();
                        Bitmap imageToStore;
                        try {
                            imageToStore = MediaStore.Images.Media.getBitmap(ManageCoursesFragment.this.requireContext().getContentResolver(), imageFileBath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        imgGallery.setImageBitmap(imageToStore);

                        imageBytes = ImageHelper.getImageBytes(imgGallery);

                        FileOutputStream fileOutputStream;
                        try {
                            fileOutputStream = new FileOutputStream("image.png");
                            fileOutputStream.write(imageBytes);
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        chooseImageLauncher.launch(intent);
    }

    // Thiết lập sự kiện cho nút đóng và xóa
    private void setPopupEventListeners(View btnClosePopup, View btnClearAllPopup) {
        btnClosePopup.setOnClickListener(v -> dialog.dismiss());
        btnClearAllPopup.setOnClickListener(v -> clearAllInputs());
    }

    // Thêm hoặc cập nhật khóa học
    private void saveCourse(int courseId) {
        // Lấy thông tin từ các trường trong popup
        String courseName = edCourseName.getText().toString().trim();
        String dayOfWeek = spinnerDayOfTheWeek.getSelectedItem().toString();
        String timeStr = edTime.getText().toString().trim();
        String durationStr = edDuration.getText().toString().trim();
        String capacityStr = edCapacity.getText().toString().trim();
        String priceStr = edPrice.getText().toString().trim();
        String courseType = courseTypeSpinner.getSelectedItem().toString();
        String description = edDescription.getText().toString().trim();
        String createdAt = DatetimeHelper.getCurrentDatetime();

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
        if (isValidateInput(courseName, timeStr, durationStr, capacityStr, priceStr, description)) {
            int duration = Integer.parseInt(durationStr);
            int capacity = Integer.parseInt(capacityStr);
            double price = Double.parseDouble(priceStr);

            // Kiểm tra thêm hoặc cập nhật khóa học
            if (courseId < 0) {
                // Thêm lớp học mới vào cơ sở dữ liệu
                courseService.addCourse(courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageBytes, price, timeStr);
            } else {
                // Cập nhật lớp học hiện có
                courseService.updateCourse(courseId, courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageBytes, price, timeStr);
            }

            DialogHelper.showSuccessDialog(getActivity(), "Course saved successfully!");

            //clear input
            clearAllInputs();

            // Nạp lại danh sách lớp học và cập nhật giao diện
            loadCourseFromDatabase();
            dialog.dismiss();
        }
    }

    // Thiết lập giao diện
    private void setPopupAddOrUpdate(int courseId) {
        if (courseId > 0) { // update
            try {
                Course findCourse = courseService.getCourse(courseId);

                tvTitle.setText("Update Course #");
                tvCourseId.setText(String.valueOf(findCourse.getCourseId()));
                tvCourseId.setVisibility(View.VISIBLE);
                tvSubtitle.setText("Update already task for your project");

                // Đặt dữ liệu khóa học hiện tại vào các trường input
                edCourseName.setText(findCourse.getCourseName());
                edTime.setText(findCourse.getTime());
                edDuration.setText(String.valueOf(findCourse.getDuration()));
                edCapacity.setText(String.valueOf(findCourse.getCapacity()));
                edPrice.setText(String.valueOf(findCourse.getPrice()));
                edDescription.setText(findCourse.getDescription());

                // Set Spinner positions
                spinnerDayOfTheWeek.setSelection(getArrayPosition("getDate", findCourse.getDayOfWeek()));
                courseTypeSpinner.setSelection(getArrayPosition("getGenre", findCourse.getCourseType()));
            } catch (Exception e) {
                e.printStackTrace(); // In lỗi ra log để kiểm tra
                DialogHelper.showErrorDialog(getActivity(), e.getMessage());
            }
        } else {  // add
            tvCourseId.setVisibility(View.GONE);
        }
    }

    private int getArrayPosition(String action, String value) {
        int position = 0;
        try {
            int arrayId = action.equals("getDate") ? R.array.day_of_the_week : R.array.class_type_options;

            // Lấy mảng từ string-array trong XML
            String[] optionsArray = getResources().getStringArray(arrayId);

            // Chuyển đổi mảng thành List để sử dụng indexOf()
            List<String> optionsList = Arrays.asList(optionsArray);

            // Tìm vị trí của courseType trong danh sách
            position = optionsList.indexOf(value);
        } catch (Exception e) {
            DialogHelper.showErrorDialog(getActivity(), "Error while getting array for spinner.");
        }

        // Trả về vị trí, hoặc -1 nếu không tìm thấy
        return position;
    }

    private boolean isValidateInput(String courseName, String timeStr, String durationStr, String capacityStr, String priceStr, String description) {
        boolean isValid = true;

        // Kiểm tra từng trường
        if (courseName.isEmpty()) {
            edCourseName.setError("Please enter course name");
            isValid = false;
        }

        if (timeStr.isEmpty()) {
            edTime.setError("Please enter time");
            isValid = false;
        } else if (!timeStr.contains(":")) {
            edTime.setError("Invalid format time, e.g: 08:30");
            isValid = false;
        }

        if (capacityStr.isEmpty()) {
            edCapacity.setError("Please enter capacity");
            isValid = false;
        } else {
            try {
                int capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    edCapacity.setError("Capacity must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                edCapacity.setError("Capacity must be a number");
                isValid = false;
            }
        }

        if (priceStr.isEmpty()) {
            edPrice.setError("Please enter price");
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    edPrice.setError("Price must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                edPrice.setError("Price must be a valid number");
                isValid = false;
            }
        }

        if (durationStr.isEmpty()) {
            edDuration.setError("Please enter duration");
            isValid = false;
        }

        if (description.isEmpty()) {
            edDescription.setError("Please enter a description");
            isValid = false;
        }

        return isValid;
    }

    public void loadCourseFromDatabase() {
        courseLists.clear();
        Cursor cursor = courseService.getAllCourses();

        if (cursor != null && cursor.moveToFirst()) {
            // Nếu có dữ liệu, nạp vào danh sách
            do {
                int id = cursor.getInt(0);
                String courseName = cursor.getString(1);
                String courseType = cursor.getString(2);
                String createdAt = cursor.getString(3);
                String dayOfWeek = cursor.getString(4);
                String description = cursor.getString(5);
                int capacity = cursor.getInt(6);
                int duration = cursor.getInt(7);
                byte[] imageUrl = cursor.getBlob(8);
                double price = cursor.getDouble(9);
                String time = cursor.getString(10);

                // Thêm course trong database vào danh sách
                courseLists.add(new Course(id, courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageUrl, price, time));
            } while (cursor.moveToNext());
        } else {
            String formattedDate = DatetimeHelper.getCurrentDatetime();
            // Pass the formatted date as a String
            byte[] img = ImageHelper.convertDrawableToByteArray(ManageCoursesFragment.this.requireContext(), R.drawable.bg_course);
            courseService.addCourse("Flow Yoga", "Beginner", formattedDate, "Monday", "A calming beginner yoga class", 20, 60, img, 15.0, "10:00");
            courseService.addCourse("Yin Yoga", "Intermediate", formattedDate, "Tuesday", "A deep stretch yoga class focusing on flexibility", 15, 75, img, 20.0, "12:00");
        }

        if (cursor != null) {
            cursor.close(); // Đóng cursor sau khi sử dụng
        }

        DialogHelper.dismissLoadingDialog();
        // Cập nhật giao diện với dữ liệu mới
        coursesAdapter = new CourseAdapter(courseLists, this);
        recyclerView.setAdapter(coursesAdapter);
    }

    // Cấu hình RecyclerView với dữ liệu mẫu
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu mẫu cho các lớp học
        courseLists = new ArrayList<>();
        coursesAdapter = new CourseAdapter(courseLists);
        recyclerView.setAdapter(coursesAdapter);
    }

    private void setEventTextChangeForSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Xử lý sự kiện clear search
                eventClearInput();

                // search
                eventSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // Tìm kiếm
    private void eventSearch(String query) {
        List<Course> filteredCourses = new ArrayList<>();

        // Lặp qua danh sách khóa học và tìm các khóa học phù hợp với query
        for (Course c : courseLists) {
            if (c.getCourseName().toLowerCase().contains(query.toLowerCase())) {
                filteredCourses.add(c);
            }
        }

        // Kiểm tra nếu không tìm thấy khóa học nào phù hợp
        if (filteredCourses.isEmpty()) {
            // Hiển thị thông báo không tìm thấy khóa học
            recyclerView.setVisibility(View.GONE);
        } else {
            // Hiển thị danh sách khóa học
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Cập nhật danh sách khóa học trong Adapter
        coursesAdapter.updateCourseList(filteredCourses);
    }

    // Nhấp vào x tại ô search để clear
    private void eventClearInput() {
        tvClearSearch.setVisibility(View.VISIBLE);
        tvClearSearch.setOnClickListener(v -> {
            searchInput.setText("");
            tvClearSearch.setVisibility(View.GONE);
        });
    }

    // Khởi tạo các View từ layout
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        add_task = view.findViewById(R.id.add_task);
        tvClearSearch = view.findViewById(R.id.clear_search);
        searchInput = view.findViewById(R.id.searchInput);

        // Khởi tạo dialog khi cần thiết
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_add_course);

        courseLists = new ArrayList<>();
        courseService = new CourseService(getContext());

        // Khởi tạo EditText trong popup
        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvSubtitle = dialog.findViewById(R.id.tvSubtitle);
        tvCourseId = dialog.findViewById(R.id.courseIdStr);
        edCourseName = dialog.findViewById(R.id.edCourseName);
        edTime = dialog.findViewById(R.id.edTime);
        edDuration = dialog.findViewById(R.id.edDuration);
        edCapacity = dialog.findViewById(R.id.edCapacity);
        edPrice = dialog.findViewById(R.id.edPrice);
        edDescription = dialog.findViewById(R.id.edDescription);
        spinnerDayOfTheWeek = dialog.findViewById(R.id.spinnerDayofTheWeek);
        courseTypeSpinner = dialog.findViewById(R.id.courseTypeSpinner);
        imgGallery = dialog.findViewById(R.id.ivUploadImage);
    }

    // Phương thức xóa tất cả thông tin nhập vào trong popup
    private void clearAllInputs() {
        edCourseName.setText("");
        spinnerDayOfTheWeek.setSelection(0);
        edTime.setText("");
        edDuration.setText("");
        edCapacity.setText("");
        edPrice.setText("");
        edDescription.setText("");
        courseTypeSpinner.setSelection(0);

        DialogHelper.showSuccessDialog(getActivity(), "All fields cleared!");
    }
}
