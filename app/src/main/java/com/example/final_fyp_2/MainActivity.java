package com.example.final_fyp_2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        ImageView profileButton = findViewById(R.id.btnProfile);
        ImageView detectButton = findViewById(R.id.btnDetect);
        ImageView otherButton = findViewById(R.id.btnOthers);
        ImageButton questionButton = findViewById(R.id.btnHelp);

        // Profile button click handler
        profileButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                profileButton.setImageResource(R.drawable.profile_click);
                Intent intent = new Intent(MainActivity.this, profile.class);
                startActivity(intent);// Change to pressed image
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                profileButton.setImageResource(R.drawable.profile_btn); // Revert to default image
            }
            return true; // Indicate the touch event is handled
        });

        detectButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                detectButton.setImageResource(R.drawable.detect_click);
                Intent intent = new Intent(MainActivity.this, detect.class);
                startActivity(intent);// Change to pressed image
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                detectButton.setImageResource(R.drawable.detect_btn); // Revert to default image
            }
            return true; // Indicate the touch event is handled
        });

        otherButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                otherButton.setImageResource(R.drawable.others_click);
                Intent intent = new Intent(MainActivity.this, other.class);
                startActivity(intent);// Change to pressed image
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                otherButton.setImageResource(R.drawable.others_btn); // Revert to default image
            }
            return true; // Indicate the touch event is handled
        });

        // Help button click handler
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformationDialog();
            }
        });




    }

    private void showInformationDialog() {
// Create a new Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);

        // Make the background of the dialog transparent (blur/dim effect)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        // Make the background behind the dialog slightly dim
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.7f;  // Dim the background
        getWindow().setAttributes(layoutParams);

        // Restore the background when the dialog is dismissed
        dialog.setOnDismissListener(dialogInterface -> {
            WindowManager.LayoutParams restoreParams = getWindow().getAttributes();
            restoreParams.alpha = 1f;  // Restore background opacity
            getWindow().setAttributes(restoreParams);
        });

        // Retrieve the infoText TextView and set the desired text
        TextView infoText = dialog.findViewById(R.id.infoText);
        infoText.setText("1.Profile is to record your allergens.\n 2.Detect is to detect allergens.\n 3.Others is for know about this application.");  // Set the text you want here

        // Show the dialog
        dialog.show();
    }
}