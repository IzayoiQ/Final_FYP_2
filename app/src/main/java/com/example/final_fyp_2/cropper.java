package com.example.final_fyp_2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;

public class cropper extends AppCompatActivity {

    private CropImageView cropImageView;
    private Uri imageUri;
    private static final int REQUEST_CAMERA_CODE = 100;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);

        cropImageView = findViewById(R.id.cropImageView);
        Button btnBack = findViewById(R.id.buttonBack);
        ImageView btnRotate = findViewById(R.id.btnRotate);
        ImageView btnFlip = findViewById(R.id.btnFlip);
        Button resultButton = findViewById(R.id.btn_result);
        ImageButton questionButton = findViewById(R.id.btnHelp);

        btnBack.setOnClickListener(v -> finish());
        btnRotate.setOnClickListener(v -> cropImageView.rotateImage(90));
        btnFlip.setOnClickListener(v -> cropImageView.setFlippedHorizontally(!cropImageView.isFlippedHorizontally()));

        resultButton.setOnClickListener(view -> {
            Bitmap croppedImage = cropImageView.getCroppedImage();

            //提取裁剪图像并执行 OCR
            if (croppedImage != null) {
                try {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedImage, 500, 500, true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] byteArray = stream.toByteArray();

                    String extractedText = getTextFromImage(croppedImage);  // Perform OCR on the cropped image/执行 OCR

                    Intent intent = new Intent(cropper.this, result.class);
                    intent.putExtra("croppedImage", byteArray);
                    intent.putExtra("extractedText", extractedText);  // Send extracted text to result activity
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("CROPPER", "Error passing image: " + e.getMessage());
                    Toast.makeText(cropper.this, "Failed to process image.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("CROPPER", "Cropped Image is null");
                Toast.makeText(cropper.this, "Error: No image was cropped.", Toast.LENGTH_SHORT).show();
            }
        });

        questionButton.setOnClickListener(view -> showInformationDialog());

        imageUri = getIntent().getParcelableExtra("imageUri");
        if (imageUri != null) {
            cropImageView.setImageUriAsync(imageUri);
        } else {
            Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
        }
    }
    private String getTextFromImage(Bitmap croppedImage) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();//对图像执行 OCR
        if (!recognizer.isOperational()) {
            Toast.makeText(cropper.this, "Error occurred while initializing OCR", Toast.LENGTH_SHORT).show();
            return "";
        } else {
            Frame frame = new Frame.Builder().setBitmap(croppedImage).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        }
    }
    private void showInformationDialog () {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.7f;
        getWindow().setAttributes(layoutParams);

        dialog.setOnDismissListener(dialogInterface -> {
            WindowManager.LayoutParams restoreParams = getWindow().getAttributes();
            restoreParams.alpha = 1f;
            getWindow().setAttributes(restoreParams);
        });

        TextView infoText = dialog.findViewById(R.id.infoText);
        infoText.setText("1.Crop Your Image.\n 2.You can rotate your image.\n 3.Click Result for the answer.");

        dialog.show();
    }
}
