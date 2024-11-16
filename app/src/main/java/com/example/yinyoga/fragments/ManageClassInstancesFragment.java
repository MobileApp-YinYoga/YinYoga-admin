package com.example.yinyoga.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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

        setEventTextChangeForSearch();
        dropdown.setOnClickListener(v -> showCustomPopupMenu(view));
        add_task.setOnClickListener(v -> openAddInstancePopup(""));
    }

    private void setEventTextChangeForSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hiển thị icon clear_search khi có nội dung
                tvClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                eventSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvClearSearch.setOnClickListener(v -> {
            searchInput.setText("");
            tvClearSearch.setVisibility(View.GONE);
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

        courseField.setOnClickListener(v -> {
            selectedField = "courseId";
            searchInput.setHint("Choose Course ID");
            searchInput.setFocusableInTouchMode(false);
            searchInput.setOnClickListener(view -> showCourseDropdown());
            popupWindow.dismiss();
        });

        instanceField.setOnClickListener(v -> {
            selectedField = "instanceId";
            searchInput.setHint("Search by Instance ID");
            searchInput.setFocusableInTouchMode(true);
            searchInput.setOnClickListener(null);
            popupWindow.dismiss();
        });

        dateField.setOnClickListener(v -> {
            selectedField = "date";
            searchInput.setHint("Click here to select date");
            searchInput.setFocusable(false);
            searchInput.setOnClickListener(view -> showDatePickerDialog());
            popupWindow.dismiss();
        });

        teacherField.setOnClickListener(v -> {
            selectedField = "teacher";
            searchInput.setHint("Search by Teacher");
            searchInput.setFocusableInTouchMode(true);
            searchInput.setOnClickListener(null);
            popupWindow.dismiss();
        });

        popupWindow.setElevation(10);
        popupWindow.showAsDropDown(anchorView, 720, 350);
    }

    private void showCourseDropdown() {
        List<Course> courseList = courseService.getAllCourses();

        // Inflate layout cho PopupWindow
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.menu_search_select_courses, null);
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_search_course);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setElevation(10);

        MenuCourseAdapter adapter = new MenuCourseAdapter(courseList, course -> {
            searchInput.setText(course.getCourseName() + " #" + course.getCourseId());
            popupWindow.dismiss(); // Đóng PopupWindow sau khi chọn khóa học
        });
        recyclerView.setAdapter(adapter);

        popupWindow.showAsDropDown(searchInput, 0, 10);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new GregorianCalendar(year, month, dayOfMonth).getTime());
            String daySuffix = getDaySuffix(dayOfMonth);
            String formattedDate = String.format("%s, %d%s %d", monthName, dayOfMonth, daySuffix, year);

            Log.d("formattedDate: ", formattedDate);
            searchInput.setText(formattedDate);
            eventSearch(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void eventSearch(String query) {
        List<ClassInstance> filteredInstances = new ArrayList<>();

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

        if (filteredInstances.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }

        instanceAdapter.updateInstanceList(filteredInstances);
    }

    public void openAddInstancePopup(String instanceId) {
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_rounded_border);

        LinearLayout btnClosePopup = dialog.findViewById(R.id.btn_close);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnClearAllPopup = dialog.findViewById(R.id.btn_clear);
        Button btnUploadImage = dialog.findViewById(R.id.btnUploadImage);

        setPopupEventListeners(btnClosePopup, btnClearAllPopup);
        btnUploadImage.setOnClickListener(this::chooseImage);
        clearAllInputs();

        setPopupAddOrUpdate(instanceId);
        isInstanceIdExists();

        btnSave.setOnClickListener(v -> saveInstance(instanceId));

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
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Check when user changes the text
                    if (!charSequence.toString().isEmpty() &&
                            tvInstanceId.getVisibility() == View.GONE &&
                            instanceService.getClassInstance(charSequence.toString()) != null) {
                        edInstanceId.setError("This ID already exists in the database");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

        }
    }

    private void setPopupEventListeners(View btnClosePopup, View btnClearAllPopup) {
        btnClosePopup.setOnClickListener(v -> dialog.dismiss());
        btnClearAllPopup.setOnClickListener(v -> clearAllInputs());
    }

    private void saveInstance(String instanceId) {
        String courseIdStr = spCourseId.getSelectedItem().toString();
        dateStr = edDate.getText().toString().trim();
        String teacher = spTeacher.getSelectedItem().toString();

        String[] splitCourseId = courseIdStr.split(" - ");
        String getCourse = splitCourseId[0];
        int courseId = Integer.parseInt(getCourse);

        if (!isValidateInput()) {
            return;
        }

        if (!Objects.equals(instanceId, "")) {
            Course course = courseService.getCourse(courseId);
            instanceService.updateClassInstance(new ClassInstance(instanceId, course, dateStr, teacher, imageBytes));
            DialogHelper.showSuccessDialog(getActivity(), "Course instance updated successfully!");
        } else {
            instanceId = edInstanceId.getText().toString().trim();

            Course course = courseService.getCourse(courseId);
            instanceService.addClassInstance(new ClassInstance(instanceId, course, dateStr, teacher, imageBytes));
            DialogHelper.showSuccessDialog(getActivity(), "Course instance saved successfully!");
        }

        loadInstancesFromDatabase();
        clearAllInputs();
        dialog.dismiss();
    }

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

                edInstanceId.setText(findInstance.getInstanceId());
                spCourseId.setSelection(getArrayPosition("getCourseId", String.valueOf(findInstance.getCourse().getCourseId())));
                edDate.setText(findInstance.getDate());
                spTeacher.setSelection(getArrayPosition("getTeacher", findInstance.getTeacher()));

            } catch (Exception e) {
                e.printStackTrace(); // In lỗi ra log để kiểm tra
                DialogHelper.showErrorDialog(getActivity(), e.getMessage());
            }
        } else {
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

        if (edInstanceId.getText().toString().trim().isEmpty()) {
            edInstanceId.setError("Please enter Instance ID");
            isValid = false;
        }

        if (edDate.getText().toString().trim().isEmpty()) {
            edDate.setError("Please enter Date");
            isValid = false;
        }

        if (imageBytes == null) {
            Toast.makeText(getActivity(), "Please upload an image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }


    public void loadInstancesFromDatabase() {
        instanceLists.clear();
        instanceLists = instanceService.getAllClassInstances();

        if (instanceLists.isEmpty()) {
            byte[] img = ImageHelper.convertDrawableToByteArray(ManageClassInstancesFragment.this.requireContext(), R.drawable.im_bg_course);
            instanceService.addClassInstance(new ClassInstance("YOGA101", courseService.getCourse(1), "January, 30th 2024", "John Doe", img));
            instanceService.addClassInstance(new ClassInstance("YOGA102", courseService.getCourse(1), "February, 15th 2024", "Jane Doe", img));
            instanceService.addClassInstance(new ClassInstance("YOGA103", courseService.getCourse(2), "March, 1st 2024", "John Doe", img));
            instanceLists = instanceService.getAllClassInstances();
            syncClassInstanceManager.syncClassInstanceFromFirestore();
        }

        syncClassInstanceManager.syncClassInstanceToFirestore();
        DialogHelper.dismissLoadingDialog();

        instanceAdapter = new ClassInstanceAdapter(instanceLists, this);
        recyclerView.setAdapter(instanceAdapter);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        instanceLists = new ArrayList<>();
        instanceAdapter = new ClassInstanceAdapter(instanceLists, this);
        recyclerView.setAdapter(instanceAdapter);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewClassInstance);
        add_task = view.findViewById(R.id.add_task);
        tvClearSearch = view.findViewById(R.id.clear_search);
        searchInput = view.findViewById(R.id.searchInput);
        dropdown = view.findViewById(R.id.dropdown_menu);

        instanceLists = new ArrayList<>();
        instanceService = new ClassInstanceService(getContext());
        courseService = new CourseService(getContext());

        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_add_instance);

        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvSubtitle = dialog.findViewById(R.id.tvSubtitle);
        tvInstanceId = dialog.findViewById(R.id.tvInstanceId);
        edInstanceId = dialog.findViewById(R.id.edInstanceId);
        spCourseId = dialog.findViewById(R.id.spCourseIdFK);
        edDate = dialog.findViewById(R.id.edDateInstance);
        spTeacher = dialog.findViewById(R.id.spinnerTeacher);
        imgGallery = dialog.findViewById(R.id.ivUploadImageClassInstance);

        arrayCourseSpinner = new ArrayList<>();

        fillToSpinnerCourseIdPopup();
        edDate.setOnClickListener(v -> showDayPickerDialog(getDayFromCourse()));
    }

    private int getDayFromCourse() {
        String selectedCourse = spCourseId.getSelectedItem().toString();
        String[] parts = selectedCourse.split(" - ");

        int courseId = Integer.parseInt(parts[0]);

        Course course = courseService.getCourse(courseId);
        String dayOfTheWeek = course.getDayOfWeek();

        Map<String, Integer> dayMap = new HashMap<>();
        dayMap.put("monday", Calendar.MONDAY);
        dayMap.put("tuesday", Calendar.TUESDAY);
        dayMap.put("wednesday", Calendar.WEDNESDAY);
        dayMap.put("thursday", Calendar.THURSDAY);
        dayMap.put("friday", Calendar.FRIDAY);
        dayMap.put("saturday", Calendar.SATURDAY);
        dayMap.put("sunday", Calendar.SUNDAY);

        return dayMap.getOrDefault(dayOfTheWeek.toLowerCase(), -1);
    }

    private void showDayPickerDialog(int dayOfWeek) {
        List<String> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        for (int i = 0; i < 10; i++) {
            dayList.add(formatDateWithSuffix(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        }

        DialogHelper.showCustomDayPickerDialog(
                getContext(),
                "Choose a " + getDayOfWeekName(dayOfWeek),
                dayList,
                selectedDate -> {
                    edDate.setText(selectedDate);
                }
        );
    }

    private String formatDateWithSuffix(Date date) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

        String day = dayFormat.format(date);
        String monthYear = monthYearFormat.format(date);

        String dayWithSuffix = day + getDaySuffix(Integer.parseInt(day));

        return monthYear.replace(",", ", " + dayWithSuffix);
    }

    private String getDaySuffix(int day) {
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

    public String getDayOfWeekName(int dayOfWeek) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        if (dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.SATURDAY) {
            return days[dayOfWeek];
        } else {
            return "Invalid day";
        }
    }

    private void fillToSpinnerCourseIdPopup() {
        arrayCourseSpinner.clear();
        // Initialize a list to store courseId - courseName
        arrayCourseSpinner = new ArrayList<>();
        List<Course> courseList = courseService.getAllCourses();

        if (courseList.isEmpty()) {
            arrayCourseSpinner.add("No data available");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrayCourseSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourseId.setAdapter(adapter);
            spCourseId.setEnabled(false);
        } else {
            for (Course course : courseList) {
                int id = course.getCourseId();
                String courseName = course.getCourseName();
                arrayCourseSpinner.add(id + " - " + courseName);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrayCourseSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourseId.setAdapter(adapter); // spinnerCoursePKId là Spinner của bạn
        }
    }

    private void clearAllInputs() {
        spCourseId.setSelection(0);
        edInstanceId.setText("");
        edDate.setText("");
        spTeacher.setSelection(0);
        edInstanceId.setError(null);
    }
}
