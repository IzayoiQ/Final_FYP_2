package com.example.final_fyp_2;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //button
        Button backButton = findViewById(R.id.buttonBack);
        ImageButton questionButton = findViewById(R.id.btnHelp);
        Button againButton = findViewById(R.id.again);
        Button doneButton = findViewById(R.id.done);
        ImageView imageView = findViewById(R.id.image_view);
        TextView conclusionTextView = findViewById(R.id.conclusion);

        //button function
        backButton.setOnClickListener(v -> finish());
        questionButton.setOnClickListener(view -> showInformationDialog());

        againButton.setOnClickListener(view -> {
            Intent intent = new Intent(result.this, detect.class);
            startActivity(intent);
        });

        doneButton.setOnClickListener(view -> {
            Intent intent = new Intent(result.this, MainActivity.class);
            startActivity(intent);
        });

        byte[] byteArray = getIntent().getByteArrayExtra("croppedImage");
        String extractedText = getIntent().getStringExtra("extractedText");  // Get the extracted text

        if (byteArray != null) {
            Bitmap croppedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(croppedImage); // Display the image
            if (!extractedText.isEmpty()) {
                Toast.makeText(result.this, "Extracted Text: " + extractedText, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(result.this, "No text detected in the image.", Toast.LENGTH_SHORT).show();
            }

            Set<String> savedAllergens = getSharedPreferences("AllergenData", MODE_PRIVATE)
                    .getStringSet("AllergenList", new HashSet<>());
            checkForAllergenMatch(extractedText, savedAllergens, conclusionTextView);
        } else {
            Toast.makeText(this, "Failed to load the cropped image.", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if the extracted text contains any saved allergens, including subtypes and synonyms
    private void checkForAllergenMatch(String extractedText, Set<String> savedAllergens, TextView conclusionTextView) {
        // Clean up the extracted text
        extractedText = cleanText(extractedText);

        // Allergen groups and their synonyms
        HashMap<String, Set<String>> allergenMap = new HashMap<>();
        allergenMap.put("Milk", new HashSet<>(Arrays.asList("Cheese", "Butter")));
        allergenMap.put("Egg", new HashSet<>(Arrays.asList("Yolk", "Egg White", "Egg Yolk", "albumen")));
        allergenMap.put("Soy", new HashSet<>(Arrays.asList("Soybean", "Soya", "Soyabean", "Soya Bean")));
        allergenMap.put("Wheat", new HashSet<>(Arrays.asList("Gluten", "Barley")));
        allergenMap.put("Tree Nut", new HashSet<>(Arrays.asList("Almond", "Brazil nut", "Cashew", "Hazelnut", "Macadamia nut", "Pecan", "Pistachio", "Walnut", "Pine nut", "Hickory nut")));
        allergenMap.put("Shellfish", new HashSet<>(Arrays.asList("Barnacle", "Crab", "Crawfish", "Krill", "Lobster", "Prawn", "Shrimp", "Abalone", "Clam", "Cockle", "Cuttlefish", "Limpet", "Mussel", "Octopus", "Oyster", "Periwinkle", "Scallop")));
        allergenMap.put("Fish", new HashSet<>(Arrays.asList("Surimi")));

        // List to store detected allergens and their subtypes
        HashMap<String, Set<String>> detectedAllergenMap = new HashMap<>();
        double similarityThreshold = 0.85; // 85% similarity threshold

        // Iterate through saved allergens and their subtypes/synonyms
        outerLoop:
        for (String allergen : savedAllergens) {
            allergen = cleanText(allergen);
            Set<String> detectedSubtypes = new HashSet<>();

            // Check exact match
            if (extractedText.contains(allergen)) {
                detectedAllergenMap.put(allergen, detectedSubtypes);
                continue;
            }

            // Check subtypes/synonyms
            if (allergenMap.containsKey(allergen)) {
                for (String subtype : allergenMap.get(allergen)) {
                    subtype = cleanText(subtype);

                    // Check for exact match or similarity match
                    if (extractedText.contains(subtype) || calculateSimilarity(extractedText, subtype) >= similarityThreshold) {
                        detectedSubtypes.add(subtype);
                    }
                }
                if (!detectedSubtypes.isEmpty()) {
                    detectedAllergenMap.put(allergen, detectedSubtypes);
                    continue outerLoop;
                }
            }

            // Check similarity for the main allergen
            if (calculateSimilarity(extractedText, allergen) >= similarityThreshold) {
                detectedAllergenMap.put(allergen, detectedSubtypes);
            }
        }

        // Build the output message
        StringBuilder detectedMessage = new StringBuilder();
        if (!detectedAllergenMap.isEmpty()) {
            detectedMessage.append("⚠️ You may be allergic to this food. Avoid eating it!\nDetected:\n");

            for (String allergen : detectedAllergenMap.keySet()) {
                detectedMessage.append("- ").append(allergen);

                Set<String> subtypes = detectedAllergenMap.get(allergen);
                if (!subtypes.isEmpty()) {
                    detectedMessage.append(" (").append(String.join(", ", subtypes)).append(")");
                }
                detectedMessage.append("\n");
            }
        } else {
            detectedMessage.append("✅ It is safe to eat.");
        }

        // Update the conclusion TextView
        conclusionTextView.setText(detectedMessage.toString().trim());
    }

    // Calculate the similarity between two strings (based on Levenshtein distance)
    private double calculateSimilarity(String text1, String text2) {
        int len1 = text1.length();
        int len2 = text2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Initialize dp array
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (text1.charAt(i - 1) == text2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                }
            }
        }

        // Calculate similarity percentage
        int maxLength = Math.max(len1, len2);
        return (1.0 - (double) dp[len1][len2] / maxLength);
    }

    // Helper method to clean text by trimming, removing extra spaces, and non-alphanumeric characters
    private String cleanText(String text) {
        // Remove leading/trailing spaces
        text = text.trim();

        // Replace multiple spaces with a single space
        text = text.replaceAll("\\s+", " ");

        // Remove non-alphanumeric characters (optional based on your needs)
        text = text.replaceAll("[^a-zA-Z0-9 ]", "");

        // Convert to lowercase for case-insensitive comparison
        return text.toLowerCase();
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
        infoText.setText("1. Click 'Again' for next detection.\n2. Click 'Done' to go back Home Page.");// Set the text you want here

        // Show the dialog
        dialog.show();
    }
}