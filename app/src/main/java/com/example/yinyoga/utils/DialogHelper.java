package com.example.yinyoga.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yinyoga.R;
import com.example.yinyoga.adapters.DateAdapter;

import java.util.List;

public class DialogHelper {
    private static Dialog loadingDialog;

    public static void showLoadingDialog(Context context, String message) {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(context);
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loadingDialog.setCancelable(false);

            View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
            TextView loadingText = view.findViewById(R.id.loading_text);
            loadingText.setText(message);

            loadingDialog.setContentView(view);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (loadingDialog != null && !loadingDialog.isShowing() && context instanceof Activity && !((Activity) context).isFinishing()) {
            loadingDialog.show();
        }
    }

    public static void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public static void showCustomDayPickerDialog(Context context, String title, List<String> dayList, OnDateSelectedListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_custom_dates);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_border);

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_dates);
        LinearLayout close = dialog.findViewById(R.id.close_choose_poup);

        close.setOnClickListener(v -> dialog.dismiss());

        dialogTitle.setText(title);

        DateAdapter dateAdapter = new DateAdapter(dayList, listener, dialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(dateAdapter);

        dialog.show();
    }

    public interface OnDateSelectedListener {
        void onDateSelected(String selectedDate);
    }

    public static void showErrorDialog(Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_error);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_border);

        TextView messageText = dialog.findViewById(R.id.dialog_message);
        messageText.setText(message);

        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);

    }

    public static void showSuccessDialog(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView messageText = dialog.findViewById(R.id.dialog_message);
        messageText.setText(message);

        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
    }

    public static void showDeleteConfirmationDialog(Activity activity, String message, final DeleteConfirmationListener listener) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_confirm_delete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView messageText = dialog.findViewById(R.id.dialog_title);
        messageText.setText(message);

        Button yesButton = dialog.findViewById(R.id.btn_yes);
        Button noButton = dialog.findViewById(R.id.btn_no);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirm();
                dialog.dismiss();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface DeleteConfirmationListener {
        void onConfirm();
    }
}
