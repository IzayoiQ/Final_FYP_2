package com.example.final_fyp_2;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class detect extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;
    private Uri photoUri;
    private static final int REQUEST_PERMISSIONS = 3;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        // Check and request permissions if necessary
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS);
        }

        // Initialize buttons and image view
        Button backButton = findViewById(R.id.buttonBack);
        ImageButton questionButton = findViewById(R.id.btnHelp);
        Button insertButton = findViewById(R.id.insert);
        ConstraintLayout cameraButtonContainer = findViewById(R.id.image_view); // ConstraintLayout container for the camera button

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Help button functionality
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformationDialog();
            }
        });

        // Camera button functionality
        cameraButtonContainer.setOnClickListener(view -> {
            // You can still use the ImageView inside the ConstraintLayout
            ImageView cameraButton = cameraButtonContainer.findViewById(R.id.cameraImageView);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Handle error
                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
                }
            }
        });

        // Insert photo button functionality
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    // Check if required permissions are granted
    private boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Create a file to store the captured image
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(detect.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle the result of the photo capture or selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    Intent cropIntent = new Intent(this, cropper.class);
                    cropIntent.putExtra("imageUri", selectedImage);
                    startActivity(cropIntent);
                }
            } else if (requestCode == CAPTURE_IMAGE) {
                if (photoUri != null) {
                    Intent cropIntent = new Intent(this, cropper.class);
                    cropIntent.putExtra("imageUri", photoUri);
                    startActivity(cropIntent);
                }
            }
        }
    }

    // Show information dialog
    private void showInformationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.7f; // Dim the background
        getWindow().setAttributes(layoutParams);

        dialog.setOnDismissListener(dialogInterface -> {
            WindowManager.LayoutParams restoreParams = getWindow().getAttributes();
            restoreParams.alpha = 1f; // Restore background opacity
            getWindow().setAttributes(restoreParams);
        });

        TextView infoText = dialog.findViewById(R.id.infoText);
        infoText.setText("1. Click Take Photo to use camera.\n2. Click Insert for select picture.");
        dialog.show();
    }
}
