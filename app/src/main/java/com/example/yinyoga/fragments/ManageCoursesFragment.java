package com.example.yinyoga.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.yinyoga.R;
import com.example.yinyoga.adapters.ClassInstanceAdapter;
import com.example.yinyoga.adapters.CourseAdapter;
import com.example.yinyoga.models.ClassInstance;
import com.example.yinyoga.models.Course;
import com.example.yinyoga.service.ClassInstanceService;
import com.example.yinyoga.service.CourseService;
import com.example.yinyoga.utils.DatetimeHelper;
import com.example.yinyoga.utils.DialogHelper;
import com.example.yinyoga.utils.ImageHelper;
import com.example.yinyoga.sync.SyncCourseManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ManageCoursesFragment extends Fragment implements CourseAdapter.CustomListeners {
    private Dialog dialog;
    private Dialog seeMoreDialog;
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
    private SyncCourseManager syncCourseManager;
    private ClassInstanceService classInstanceService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadCourseFromDatabase();

        setEventTextChangeForSearch();
        add_task.setOnClickListener(v -> openAddClassPopup(-1));
    }

    public void openAddClassPopup(int courseId) {
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_rounded_border);
        LinearLayout btnClosePopup = dialog.findViewById(R.id.btnClose);

        Button btnSave = dialog.findViewById(R.id.btnSaveCourse);
        Button btnClearAllPopup = dialog.findViewById(R.id.btnClearAll);
        TextView btnUploadImage = dialog.findViewById(R.id.btnUploadImage);

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

    private void setPopupEventListeners(View btnClosePopup, View btnClearAllPopup) {
        btnClosePopup.setOnClickListener(v -> dialog.dismiss());
        btnClearAllPopup.setOnClickListener(v -> clearAllInputs(true));
    }

    private void saveCourse(int courseId) {
        if (!isValidateInput()) {
            return;
        }

        String courseName = edCourseName.getText().toString().trim();
        String dayOfWeek = spinnerDayOfTheWeek.getSelectedItem().toString();
        String timeStr = edTime.getText().toString().trim();
        String durationStr = edDuration.getText().toString().trim();
        String capacityStr = edCapacity.getText().toString().trim();
        String priceStr = edPrice.getText().toString().trim();
        String courseType = courseTypeSpinner.getSelectedItem().toString();
        String description = edDescription.getText().toString().trim();
        String createdAt = DatetimeHelper.getCurrentDatetime();

        String message = "Your inputted data:";
        message += "\nName: " + courseName;
        message += "\nDay of the week: " + dayOfWeek;
        message += "\nTime: " + timeStr;
        message += "\nDuration: " + durationStr;
        message += "\nCapacity: " + capacityStr;
        message += "\nPrice: " + priceStr;
        message += "\nType: " + courseType;
        message += "\nDescription: " + description;
        message += "\nDo you want to save this course?";

        DialogHelper.showConfirmationDialog(
                requireActivity(),
                "Confirm your course!",
                message,
                imageBytes,
                () -> {
                    int duration = Integer.parseInt(durationStr);
                    int capacity = Integer.parseInt(capacityStr);
                    double price = Double.parseDouble(priceStr);

                    Course course = new Course(courseName, courseType, createdAt, dayOfWeek, description, capacity, duration, imageBytes, price, timeStr);

                    if (courseId < 0) {
                        courseService.addCourse(course);
                    } else {
                        course.setCourseId(courseId);
                        courseService.updateCourse(course);
                    }

                    DialogHelper.showSuccessDialog(getActivity(), "Course saved successfully!");

                    clearAllInputs(false);
                    loadCourseFromDatabase();

                    dialog.dismiss();
                });
    }

    private void setPopupAddOrUpdate(int courseId) {
        if (courseId > 0) {
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

                imgGallery.setImageBitmap(ImageHelper.convertByteArrayToBitmap(findCourse.getImageUrl()));
                imageBytes = ImageHelper.getImageBytes(imgGallery);
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showErrorDialog(getActivity(), e.getMessage());
            }
        } else {
            tvCourseId.setVisibility(View.GONE);
            clearAllInputs(false);
        }
    }

    private int getArrayPosition(String action, String value) {
        int position = 0;
        try {
            int arrayId = action.equals("getDate") ? R.array.day_of_the_week : R.array.class_type_options;

            // Get array from string-array in XML
            String[] optionsArray = getResources().getStringArray(arrayId);

            // Convert array to List to use indexOf()
            List<String> optionsList = Arrays.asList(optionsArray);

            // Find the position of courseType in the list
            position = optionsList.indexOf(value);
        } catch (Exception e) {
            DialogHelper.showErrorDialog(getActivity(), "Error while getting array for spinner.");
        }

        return position;
    }

    private boolean isValidateInput() {
        boolean isValid = true;

        String courseName = edCourseName.getText().toString().trim();
        String timeStr = edTime.getText().toString().trim();
        String durationStr = edDuration.getText().toString().trim();
        String capacityStr = edCapacity.getText().toString().trim();
        String priceStr = edPrice.getText().toString().trim();
        String description = edDescription.getText().toString().trim();

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

        if (imageBytes == null) {
            Toast.makeText(getActivity(), "Please upload an image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    public void loadCourseFromDatabase() {
        syncCourseManager.syncCoursesToFirestore();
        syncCourseManager.syncCourseFromFirestore();

        courseLists.clear();
        courseLists = courseService.getAllCourses();

        if (!courseLists.isEmpty()) {
            coursesAdapter = new CourseAdapter(courseLists, this);
            coursesAdapter.setCustomListeners(this);
            recyclerView.setAdapter(coursesAdapter);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                eventClearInput();
                eventSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void eventSearch(String query) {
        List<Course> filteredCourses = new ArrayList<>();

        for (Course c : courseLists) {
            if (c.getCourseName().toLowerCase().contains(query.toLowerCase())) {
                filteredCourses.add(c);
            }
        }

        if (filteredCourses.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }

        coursesAdapter.updateCourseList(filteredCourses);
    }

    private void eventClearInput() {
        tvClearSearch.setVisibility(View.VISIBLE);
        tvClearSearch.setOnClickListener(v -> {
            searchInput.setText("");
            tvClearSearch.setVisibility(View.GONE);
        });
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewCourse);
        add_task = view.findViewById(R.id.add_task);
        tvClearSearch = view.findViewById(R.id.clear_search);
        searchInput = view.findViewById(R.id.searchInput);

        // Initialize the dialog when needed
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_add_course);

        courseLists = new ArrayList<>();
        courseService = new CourseService(getContext());
        classInstanceService = new ClassInstanceService(getContext());

        // Initialize EditTexts in the popup
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

        syncCourseManager = new SyncCourseManager(requireContext());
    }

    private void clearAllInputs(boolean showNotification) {
        edCourseName.setText("");
        edTime.setText("");
        edDuration.setText("");
        edCapacity.setText("");
        edPrice.setText("");
        edDescription.setText("");

        // Reset spinners to their default selections
        spinnerDayOfTheWeek.setSelection(0);
        courseTypeSpinner.setSelection(0);

        // Clear the image view
        imgGallery.setImageBitmap(null);

        // Clear error states (if any)
        edCourseName.setError(null);
        edTime.setError(null);
        edDuration.setError(null);
        edCapacity.setError(null);
        edPrice.setError(null);
        edDescription.setError(null);
        
        if (showNotification) {
            DialogHelper.showSuccessDialog(getActivity(), "All inputs cleared successfully!");
        }
    }

    @Override
    public void onSeeMoreClick(int courseId) {
        // Initialize the dialog when needed
        seeMoreDialog = new Dialog(requireContext());
        seeMoreDialog.setContentView(R.layout.popup_view_detail);
        Objects.requireNonNull(seeMoreDialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_rounded_border);

        LinearLayout btnCloseSeeMorePopup = seeMoreDialog.findViewById(R.id.btnCloseSeeMore);
        RecyclerView recyclerViewSeeMore = seeMoreDialog.findViewById(R.id.recyclerViewSeeMore);

        btnCloseSeeMorePopup.setOnClickListener(v -> seeMoreDialog.dismiss());
        recyclerViewSeeMore.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Sample data for class instances
        Course course = courseService.getCourse(courseId);
        List<ClassInstance> classInstanceList = classInstanceService.getClassInstancesByCourseId(courseId);
        for (ClassInstance classInstance : classInstanceList) {
            classInstance.setCourse(course);
        }
        ManageClassInstancesFragment ManageClassInstancesFragment = new ManageClassInstancesFragment();
        ClassInstanceAdapter classInstanceAdapter = new ClassInstanceAdapter(classInstanceList, ManageClassInstancesFragment);
        recyclerViewSeeMore.setAdapter(classInstanceAdapter);

        seeMoreDialog.show();
    }

    @Override
    public void handleDeleteAction(int position) {
        Course course = courseLists.get(position);
        DialogHelper.showConfirmationDialog(
                requireActivity(),
                "Are you sure you want to delete course \"" + course.getCourseName() + "\"?",
                null,
                null,
                () -> {
                    // Delete course and refresh list
                    courseService.deleteCourse(course.getCourseId());
                    courseLists.remove(position);
                    syncCourseManager.deleteCourseOnFirebase(course.getCourseId());

                    loadCourseFromDatabase();
                    DialogHelper.showSuccessDialog(requireActivity(), "Course removed successfully!");
                });
    }
}
