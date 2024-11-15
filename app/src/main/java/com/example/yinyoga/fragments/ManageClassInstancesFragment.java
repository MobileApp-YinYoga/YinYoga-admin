package com.example.yinyoga.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.adapters.ClassInstanceAdapter;
import com.example.yinyoga.adapters.MenuCourseAdapter;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.service.ClassInstanceService;
import com.example.yinyoga.service.CourseService;
import com.example.yinyoga.sync.SyncClassInstanceManager;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.ImageHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ManageClassInstancesFragment extends Fragment {
    private Dialog dialog;
    private RecyclerView recyclerView;
    private ClassInstanceAdapter instanceAdapter;
    private List<ClassInstance> instanceLists;
    private LinearLayout add_task, dropdown;
    private Spinner spTeacher, spCourseId;
    private EditText edDate, edInstanceId;
    private EditText searchInput;
    private TextView tvInstanceId, tvTitle, tvSubtitle, tvClearSearch;
    private String selectedField;
    private String dateStr;
    private ClassInstanceService instanceService;
    private CourseService courseService;
    private List<String> arrayCourseSpinner;
    private ImageView imgGallery;
    private byte[] imageBytes;
    SyncClassInstanceManager syncClassInstanceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout của màn hình "Manage Class Instances"
        return inflater.inflate(R.layout.fragment_manage_class_instances, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DialogHelper.showLoadingDialog(this.getContext(), "Loading all class instances...");

        syncClassInstanceManager = new SyncClassInstanceManager(requireContext());
        initViews(view);
        setupRecyclerView();
        loadInstancesFromDatabase();

        setEventTextChangeForSearch();  // Xử lý sự kiện search

        dropdown.setOnClickListener(v -> showCustomPopupMenu(view)); // sự kiện chọn option search

        // Xử lý sự kiện thêm phiên học mới
        add_task.setOnClickListener(v -> openAddInstancePopup(""));
    }

    private void setEventTextChangeForSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hiển thị icon clear_search khi có nội dung
                tvClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                eventSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý sự kiện khi nhấn vào icon clear_search
        tvClearSearch.setOnClickListener(v -> {
            searchInput.setText(""); // Xóa văn bản trong searchInput
            tvClearSearch.setVisibility(View.GONE); // Ẩn icon clear_search
        });
    }

    private void showCustomPopupMenu(View anchorView) {
        // Inflate the custom layout
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.menu_search_instances, null);

        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        TextView courseField = popupView.findViewById(R.id.course_field);
        TextView instanceField = popupView.findViewById(R.id.instance_field);
        TextView dateField = popupView.findViewById(R.id.date_field);
        TextView teacherField = popupView.findViewById(R.id.teacher_field);

        // Lắng nghe sự kiện khi người dùng chọn trường để tìm kiếm
        courseField.setOnClickListener(v -> {
            selectedField = "courseId";
            searchInput.setHint("Choose Course ID");
            searchInput.setFocusableInTouchMode(false); // Cho phép nhập liệu
            searchInput.setOnClickListener(view -> showCourseDropdown());
            popupWindow.dismiss();
        });

        instanceField.setOnClickListener(v -> {
            selectedField = "instanceId";
            searchInput.setHint("Search by Instance ID");
            searchInput.setFocusableInTouchMode(true); // Cho phép nhập liệu
            searchInput.setOnClickListener(null); // Bỏ lắng nghe DatePickerDialog
            popupWindow.dismiss();
        });

        dateField.setOnClickListener(v -> {
            selectedField = "date";
            searchInput.setHint("Click here to select date");
            searchInput.setFocusable(false); // Không cho phép nhập liệu trực tiếp
            searchInput.setOnClickListener(view -> showDatePickerDialog()); // Mở DatePickerDialog khi nhấn vào
            popupWindow.dismiss();
        });

        teacherField.setOnClickListener(v -> {
            selectedField = "teacher";
            searchInput.setHint("Search by Teacher");
            searchInput.setFocusableInTouchMode(true); // Cho phép nhập liệu
            searchInput.setOnClickListener(null); // Bỏ lắng nghe DatePickerDialog
            popupWindow.dismiss();
        });

        // Show the PopupWindow
        popupWindow.setElevation(10);
        popupWindow.showAsDropDown(anchorView, 720, 350);
    }

    private void showCourseDropdown() {
        // Tạo danh sách khóa học từ cơ sở dữ liệu
        Cursor cursor = courseService.getAllCourses();
        List<Course> courseList = new ArrayList<>();

        // Duyệt qua các khóa học trong cơ sở dữ liệu và thêm vào courseList
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int courseId = cursor.getInt(0);
                String courseName = cursor.getString(1);

                courseList.add(new Course(courseId, courseName)); // Thêm đối tượng Course vào danh sách
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Inflate layout cho PopupWindow
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.menu_search_select_courses, null);
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_search_course);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo PopupWindow trước khi sử dụng trong adapter
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setElevation(10);

        // Tạo Adapter và set vào RecyclerView
        MenuCourseAdapter adapter = new MenuCourseAdapter(courseList, course -> {
            // Cập nhật searchInput với tên và ID khóa học khi chọn một khóa học
            searchInput.setText(course.getCourseName() + " #" + course.getCourseId());
            popupWindow.dismiss(); // Đóng PopupWindow sau khi chọn khóa học
        });
        recyclerView.setAdapter(adapter);

        // Hiển thị PopupWindow
        popupWindow.showAsDropDown(searchInput, 0, 10);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            // Định dạng ngày đã chọn với định dạng "January 30th, 2024"
            String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new GregorianCalendar(year, month, dayOfMonth).getTime());
            String daySuffix = getDaySuffix(dayOfMonth);
            String formattedDate = String.format("%s, %d%s %d", monthName, dayOfMonth, daySuffix, year);

            Log.d("formattedDate: ", formattedDate);
            // Hiển thị ngày vào searchInput và thực hiện tìm kiếm
            searchInput.setText(formattedDate);
            eventSearch(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void eventSearch(String query) {
        List<ClassInstance> filteredInstances = new ArrayList<>();

        // Lặp qua danh sách các phiên học và tìm các phiên học phù hợp với query
        for (ClassInstance instance : instanceLists) {
            switch (selectedField) {
                case "courseId":
                    if (query.contains(String.valueOf(instance.getCourse().getCourseId()))) {
                        filteredInstances.add(instance);
                    }
                    break;
                case "instanceId":
                    if (instance.getInstanceId().contains(query)) {
                        filteredInstances.add(instance);
                    }
                    break;
                case "date":
                    if (instance.getDate().contains(query)) {
                        filteredInstances.add(instance);
                    }
                    break;

                case "teacher":
                    if (instance.getTeacher().toLowerCase().contains(query.toLowerCase())) {
                        filteredInstances.add(instance);
                    }
                    break;
            }
        }

        // Kiểm tra nếu không tìm thấy phiên học nào phù hợp
        if (filteredInstances.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Cập nhật danh sách phiên học trong Adapter
        instanceAdapter.updateInstanceList(filteredInstances);
    }

    // Mở popup để thêm hoặc cập nhật phiên học
    public void openAddInstancePopup(String instanceId) {
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_rounded_border);

        // Thiết lập sự kiện cho nút đóng
        LinearLayout btnClosePopup = dialog.findViewById(R.id.btn_close);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnClearAllPopup = dialog.findViewById(R.id.btn_clear);
        Button btnUploadImage = dialog.findViewById(R.id.btnUploadImage);

        // Đặt sự kiện cho nút đóng và xóa
        setPopupEventListeners(btnClosePopup, btnClearAllPopup);

        btnUploadImage.setOnClickListener(this::chooseImage);

        clearAllInputs();

        // Kiểm tra xem có phải cập nhật không, để điền vào ô input
        setPopupAddOrUpdate(instanceId);

        // bắt sự kiện trùng id
        isInstanceIdExists();

        // Sự kiện khi nhấn Save
        btnSave.setOnClickListener(v -> saveInstance(instanceId));

        // Mở popup
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
                            imageToStore = MediaStore.Images.Media.getBitmap(ManageClassInstancesFragment.this.requireContext().getContentResolver(), imageFileBath);
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

    private void isInstanceIdExists() {
        if (edInstanceId.isEnabled()) {
            edInstanceId.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Không cần xử lý sự kiện này
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Kiểm tra khi người dùng thay đổi văn bản
                    if (!charSequence.toString().isEmpty() && tvInstanceId.getVisibility() == View.GONE ) {
                        checkAlreadyId(charSequence.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Không cần xử lý sự kiện này
                }
            });

        }
    }

    private boolean checkAlreadyId(String id) {
        boolean idExists = instanceService.getClassInstance(id) != null;

        // Thiết lập lỗi nếu ID tồn tại
        edInstanceId.setError(idExists ? "This ID already exists in the database" : null);

        return idExists;
    }

    // Thiết lập sự kiện cho nút đóng và xóa
    private void setPopupEventListeners(View btnClosePopup, View btnClearAllPopup) {
        btnClosePopup.setOnClickListener(v -> dialog.dismiss());
        btnClearAllPopup.setOnClickListener(v -> clearAllInputs());
    }

    // Thêm hoặc cập nhật phiên học
    private void saveInstance(String instanceId) {
        // Lấy thông tin từ các trường trong popup
        String courseIdStr = spCourseId.getSelectedItem().toString();
        dateStr = edDate.getText().toString().trim();
        String teacher = spTeacher.getSelectedItem().toString();

        String[] splitCourseId = courseIdStr.split(" - ");
        String getCourse = splitCourseId[0];
        int courseId = Integer.parseInt(getCourse);

        if (!isValidateInput()) {
            return; // Nếu không hợp lệ, không thực hiện lưu
        }
        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
        if (!Objects.equals(instanceId, "")) {
            // Cập nhật phiên học hiện có
            instanceService.updateClassInstance(instanceId, courseId, dateStr, teacher, imageBytes);
            DialogHelper.showSuccessDialog(getActivity(), "Course instance updated successfully!");
        } else {
            instanceId = edInstanceId.getText().toString().trim();

            // Thêm phiên học mới vào cơ sở dữ liệu
            instanceService.addClassInstance(instanceId, courseId, dateStr, teacher, imageBytes);
            DialogHelper.showSuccessDialog(getActivity(), "Course instance saved successfully!");
        }

        loadInstancesFromDatabase();
        clearAllInputs();
        dialog.dismiss();
    }

    // Thiết lập giao diện
    private void setPopupAddOrUpdate(String instanceId) {
        edInstanceId.setEnabled(true);

        if (!Objects.equals(instanceId, "")) { // update
            try {
                ClassInstance findInstance = instanceService.getClassInstance(instanceId);

                edInstanceId.setEnabled(false);
                tvTitle.setText("Update Class Instance");
                tvSubtitle.setText("Update class instance #");
                tvInstanceId.setText(findInstance.getInstanceId());
                tvInstanceId.setVisibility(View.VISIBLE);

                // Đặt dữ liệu phiên học hiện tại vào các trường input
                edInstanceId.setText(findInstance.getInstanceId());
                spCourseId.setSelection(getArrayPosition("getCourseId", String.valueOf(findInstance.getCourse().getCourseId())));
                edDate.setText(findInstance.getDate());
                spTeacher.setSelection(getArrayPosition("getTeacher", findInstance.getTeacher()));

            } catch (Exception e) {
                e.printStackTrace(); // In lỗi ra log để kiểm tra
                DialogHelper.showErrorDialog(getActivity(), e.getMessage());
            }
        } else{
            tvTitle.setText("Add Class Instance");
            tvSubtitle.setText("Create a new course for YinYoga.");
            tvInstanceId.setVisibility(View.GONE);

            clearAllInputs();
        }
    }

    private int getArrayPosition(String action, String value) {
        try {
            List<String> optionsList = action.equals("getTeacher")
                    ? Arrays.asList(getResources().getStringArray(R.array.teacher_options))
                    : arrayCourseSpinner;

            for (int i = 0; i < optionsList.size(); i++) {
                String item = optionsList.get(i);

                if (action.equals("getTeacher") && item.contains(value)) {
                    return i;
                } else if (action.equals("getCourseId") && item.split(" - ")[0].equals(value)) {
                    return i;
                }
            }

            throw new Exception("Không tìm thấy phần tử phù hợp trong mảng");
        } catch (Exception e) {
            DialogHelper.showErrorDialog(getActivity(), "Error while getting array for spinner: " + e.getMessage());
        }
        return -1;
    }

    private boolean isValidateInput() {
        boolean isValid = true;

        // Kiểm tra nếu InstanceId rỗng
        if (edInstanceId.getText().toString().trim().isEmpty()) {
            edInstanceId.setError("Please enter Instance ID");
            isValid = false;
        }

        // Kiểm tra nếu ngày (date) rỗng
        if (edDate.getText().toString().trim().isEmpty()) {
            edDate.setError("Please enter Date");
            isValid = false;
        }

        // Kiểm tra nếu chưa chọn hình ảnh
        if (imageBytes == null) {
            Toast.makeText(getActivity(), "Please upload an image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }


    public void loadInstancesFromDatabase() {
        instanceLists.clear();
        Cursor cursor = instanceService.getAllClassInstances();

        if (cursor != null && cursor.moveToFirst()) {
            // Nếu có dữ liệu, nạp vào danh sách
            do {
                String instanceId = cursor.getString(0);
                int courseId = cursor.getInt(1);
                String date = cursor.getString(2);
                String teacher = cursor.getString(3);
                byte[] imageUrl = cursor.getBlob(4);
                String courseName = cursor.getString(5);

                // Tạo đối tượng Course và gán courseId, courseName
                Course course = new Course();
                course.setCourseId(courseId);
                course.setCourseName(courseName);

                // Thêm instance vào danh sách
                instanceLists.add(new ClassInstance(instanceId, course, date, teacher, imageUrl));
            } while (cursor.moveToNext());
            syncClassInstanceManager.syncClassInstanceToFirestore();
        } else {
            byte[] img = ImageHelper.convertDrawableToByteArray(ManageClassInstancesFragment.this.requireContext(), R.drawable.bg_course);
            instanceService.addClassInstance("YOGA101", 1,"January, 30th 2024", "John Doe", img);
            instanceService.addClassInstance("YOGA102", 1, "February, 15th 2024", "Jane Smith", img);
            instanceService.addClassInstance("YOGA103", 1,"March, 5st 2024", "Albus Dumbledore", img);
            syncClassInstanceManager.syncClassInstanceFromFirestore();
        }

        if (cursor != null) {
            cursor.close(); // Đóng cursor sau khi sử dụng
        }

        DialogHelper.dismissLoadingDialog();
        // Cập nhật giao diện với dữ liệu mới
        instanceAdapter = new ClassInstanceAdapter(instanceLists, this);
        recyclerView.setAdapter(instanceAdapter);
    }

    // Cấu hình RecyclerView với dữ liệu mẫu
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu mẫu cho các phiên học
        instanceLists = new ArrayList<>();
        instanceAdapter = new ClassInstanceAdapter(instanceLists, this);
        recyclerView.setAdapter(instanceAdapter);
    }

    // Khởi tạo các View từ layout
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        add_task = view.findViewById(R.id.add_task);
        tvClearSearch = view.findViewById(R.id.clear_search);
        searchInput = view.findViewById(R.id.searchInput);
        dropdown = view.findViewById(R.id.dropdown_menu);

        instanceLists = new ArrayList<>();
        instanceService = new ClassInstanceService(getContext());
        courseService = new CourseService(getContext());

        // Khởi tạo dialog khi cần thiết
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_add_instance);

        // Khởi tạo EditText trong popup
        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvSubtitle = dialog.findViewById(R.id.tvSubtitle);
        tvInstanceId = dialog.findViewById(R.id.tvInstanceId);
        edInstanceId = dialog.findViewById(R.id.edInstanceId);
        spCourseId = dialog.findViewById(R.id.spCourseIdFK);
        edDate = dialog.findViewById(R.id.edDateInstance);
        spTeacher = dialog.findViewById(R.id.spinnerTeacher);
        imgGallery = dialog.findViewById(R.id.ivUploadImageClassInstance);

        arrayCourseSpinner = new ArrayList<>();

        // Lấy courseId - courseName vào array
        fillToSpinnerCourseIdPopup();
        // Khi nhấp vào ô ngày
        edDate.setOnClickListener(v -> showDayPickerDialog(getDayFromCourse()));
    }

    private int getDayFromCourse() {
        // Lấy giá trị courseId từ Spinner (spinnerCourseId là Spinner hiển thị Course ID)
        String selectedCourse = spCourseId.getSelectedItem().toString(); // Lấy giá trị "1 - Flow Yoga" từ Spinner
        String[] parts = selectedCourse.split(" - "); // Tách phần id và tên

        int courseId = Integer.parseInt(parts[0]); // Lấy courseId

        // Lấy thông tin dayOfTheWeek từ courseService bằng cách sử dụng courseId
        Course course = courseService.getCourse(courseId); // Giả sử bạn đã có hàm này trong service
        String dayOfTheWeek = course.getDayOfWeek();

        // Sử dụng một Map để ánh xạ tên các ngày thành giá trị Calendar
        Map<String, Integer> dayMap = new HashMap<>();
        dayMap.put("monday", Calendar.MONDAY);
        dayMap.put("tuesday", Calendar.TUESDAY);
        dayMap.put("wednesday", Calendar.WEDNESDAY);
        dayMap.put("thursday", Calendar.THURSDAY);
        dayMap.put("friday", Calendar.FRIDAY);
        dayMap.put("saturday", Calendar.SATURDAY);
        dayMap.put("sunday", Calendar.SUNDAY);

        // Trả về giá trị tương ứng với dayOfTheWeek, hoặc -1 nếu không tìm thấy
        return dayMap.getOrDefault(dayOfTheWeek.toLowerCase(), -1);
    }

    //Hiển thị lịch có thứ theo courseId
    private void showDayPickerDialog(int dayOfWeek) {
        // Tạo danh sách các ngày tương ứng với dayOfWeek bắt đầu từ ngày hiện tại
        List<String> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Di chuyển đến ngày tương ứng tiếp theo nếu không phải là ngày cần tìm
        while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Thêm 10 ngày tương ứng tiếp theo vào danh sách
        for (int i = 0; i < 10; i++) {
            dayList.add(formatDateWithSuffix(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        }

        // Hiển thị dialog custom
        DialogHelper.showCustomDayPickerDialog(
                getContext(),
                "Choose a " + getDayOfWeekName(dayOfWeek),
                dayList,
                selectedDate -> {
                    // Cập nhật ngày đã chọn vào EditText
                    edDate.setText(selectedDate);
                }
        );
    }

    private String formatDateWithSuffix(Date date) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

        // Lấy ngày, tháng và năm
        String day = dayFormat.format(date);
        String monthYear = monthYearFormat.format(date);

        // Thêm hậu tố thích hợp
        String dayWithSuffix = day + getDaySuffix(Integer.parseInt(day));

        // Kết hợp để tạo định dạng mong muốn
        return monthYear.replace(",", ", " + dayWithSuffix);
    }

    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    // lấy day name
    public String getDayOfWeekName(int dayOfWeek) {
        // Mảng các tên ngày trong tuần, chỉ số bắt đầu từ 1 (Sunday)
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        if (dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.SATURDAY) {
            return days[dayOfWeek];
        } else {
            return "Invalid day"; // Nếu dayOfWeek không hợp lệ
        }
    }

    private void fillToSpinnerCourseIdPopup() {
        arrayCourseSpinner.clear();
        // Khởi tạo danh sách để lưu các giá trị courseId - courseName
        arrayCourseSpinner = new ArrayList<>();

        // Lấy dữ liệu từ courseService
        Cursor cursor = courseService.getAllCourses();

        if (cursor != null && cursor.moveToFirst()) {
            // Nếu có dữ liệu, nạp vào danh sách
            do {
                int id = cursor.getInt(0); // Lấy courseId
                String courseName = cursor.getString(1); // Lấy courseName

                // Thêm courseId - courseName vào danh sách
                arrayCourseSpinner.add(id + " - " + courseName);

            } while (cursor.moveToNext());

            // Sau khi có danh sách, đưa vào Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrayCourseSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourseId.setAdapter(adapter); // spinnerCoursePKId là Spinner của bạn
        } else {
            // Nếu không có dữ liệu, hiển thị thông báo không có dữ liệu và vô hiệu hóa Spinner
            arrayCourseSpinner.add("No data available");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrayCourseSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourseId.setAdapter(adapter);
            spCourseId.setEnabled(false); // Vô hiệu hóa Spinner nếu không có dữ liệu
        }

        if (cursor != null) {
            cursor.close(); // Đóng cursor sau khi sử dụng
        }
    }

    // Phương thức xóa tất cả thông tin nhập vào trong popup
    private void clearAllInputs() {
        spCourseId.setSelection(0);
        edInstanceId.setText("");
        edDate.setText("");
        spTeacher.setSelection(0);
        edInstanceId.setError(null);
    }
}
