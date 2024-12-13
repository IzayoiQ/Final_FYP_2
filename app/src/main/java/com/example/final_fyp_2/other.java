package com.example.final_fyp_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class other extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);


        Button backButton = findViewById(R.id.buttonBack);
        ImageButton questionButton = findViewById(R.id.btnHelp);
        Button buttonAboutUs = findViewById(R.id.buttonAboutUs);
        Button help= findViewById(R.id.buttonHelpPage);


        //back button function
        backButton.setOnClickListener(v -> finish());

        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformationDialog1();
            }
        });

        buttonAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformationDialog2();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformationDialog3();
            }
        });
    }

    private void showInformationDialog1() {
        // Create a new Dialog for the help/info
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
        infoText.setText("1.About us is vision and mission. \n 2. Help is some Q&A.");  // Set the text you want here

        // Show the dialog
        dialog.show();
    }
    private void showInformationDialog2() {
        // Create a new Dialog for the help/info
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
        infoText.setText("Vision:  Become the world's leading allergen detection tool to ensure everyone's food safety.\n\n Mission: Help people with food allergies around the world reduce risks and improve their quality of life and happiness.");  // Set the text you want here

        // Show the dialog
        dialog.show();
    }
    private void showInformationDialog3() {
        // Create a new Dialog for the help/info
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
        infoText.setText("\"Did You Know?\"\n" +
                "\"About 10% of the world's population has food allergies, with peanut allergy being the most common.\"\n\n" +
                "\"Many food ingredients may hide allergens, such as lactose in dairy products.\"");  // Set the text you want here

        // Show the dialog
        dialog.show();
    }

}