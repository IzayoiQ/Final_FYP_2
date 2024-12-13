package com.example.final_fyp_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class profile extends AppCompatActivity {

    private LinearLayout layout2;
    private int allergenCounter;
    private SharedPreferences sharedPreferences;
    private final String[] prePreparedAllergens = {"Egg", "Fish", "Milk", "Peanut", "Shellfish", "Soy", "Tree Nut", "Wheat"};
    private final HashMap<String, List<String>> allergenSynonyms = new HashMap<String, List<String>>() {{
        put("Milk", Arrays.asList("Cheese", "Butter"));
        put("Egg", Arrays.asList("Yolk", "Egg White", "Egg Yolk", "Albumen"));
        put("Soy", Arrays.asList("Soybean", "Soya", "Soyabean", "Soya Bean"));
        put("Wheat", Arrays.asList("Gluten", "Barley"));
        put("Tree Nut", Arrays.asList("Almond", "Brazil nut", "Cashew", "Hazelnut", "Macadamia nut",
                "Pecan", "Pistachio", "Walnut", "Pine nut", "Hickory nut"));
        put("Shellfish", Arrays.asList("Barnacle", "Crab", "Crawfish", "Krill", "Lobster", "Prawn", "Shrimp",
                "Abalone", "Clam", "Cockle", "Cuttlefish", "Limpet", "Mussel",
                "Octopus", "Oyster", "Periwinkle", "Scallop"));
        put("Fish", Arrays.asList("Surimi"));
        put("Peanut", new ArrayList<>()); // No synonyms
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("AllergenData", MODE_PRIVATE);

        Button backButton = findViewById(R.id.buttonBack);
        Button insertButton = findViewById(R.id.buttonInsert);
        Button saveButton = findViewById(R.id.buttonSave);
        Button resetButton = findViewById(R.id.buttonReset);
        ImageButton questionButton = findViewById(R.id.btnHelp);
        EditText inputField = findViewById(R.id.input_field);
        layout2 = findViewById(R.id.layout2);

        // Always display pre-prepared allergens if nothing is saved
        if (!sharedPreferences.contains("PrimaryAllergenList")) {
            displayPrePreparedAllergens();
        } else {
            displayAllergensWithSynonyms();
        }

        backButton.setOnClickListener(v -> navigateToMainActivity());

        questionButton.setOnClickListener(view -> showInformationDialog());

        saveButton.setOnClickListener(view -> {
            saveAllergensToPreferences();
            Toast.makeText(this, "Allergen data saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(profile.this, allergenSynonym.class);
            startActivity(intent);  // Navigate to the allergenSynonyms page
        });

        resetButton.setOnClickListener(view -> {
            layout2.removeAllViews();  // Clear the current allergen list
            allergenCounter = 0;  // Reset allergen counter

            // Clear SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            if (editor.commit()) {
                Toast.makeText(this, "Preferences cleared!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to clear preferences!", Toast.LENGTH_SHORT).show();
            }

            // Display pre-prepared allergens
            displayPrePreparedAllergens();  // Show default allergens

            Toast.makeText(this, "Allergen data reset!", Toast.LENGTH_SHORT).show();
        });


        insertButton.setOnClickListener(view -> {
            String allergenName = inputField.getText().toString().trim();
            if (!TextUtils.isEmpty(allergenName)) {
                if (checkForDuplicate(allergenName)) {
                    Toast.makeText(profile.this, "Allergen already exists in the list.", Toast.LENGTH_SHORT).show(); // Display a toast message for duplicates
                } else {
                    // If no duplicate, add the allergen
                    addAllergen(allergenName, true);
                    inputField.setText(""); // Clear the input field

                    // Save the updated allergen list to SharedPreferences
                    saveAllergensToPreferences();

                    // Refresh synonym display after insertion
                    Toast.makeText(profile.this, "Allergen added!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(profile.this, "Please enter an allergen.", Toast.LENGTH_SHORT).show(); // Optional: Prompt if input is empty
            }
        });
    }

    // Override the back button behavior to navigate to MainActivity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToMainActivity();
    }

    // Navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(profile.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void displayPrePreparedAllergens() {
        for (String allergen : prePreparedAllergens) {
            addAllergen(allergen, false);
        }
    }

    private void displayAllergensWithSynonyms() {
        Intent intent = new Intent(profile.this, allergenSynonym.class);
        startActivity(intent);  // Navigate to the allergenSynonyms page
    }

    private void addAllergen(String allergenName, boolean isChecked) {
        if (checkForDuplicate(allergenName)) return;

        LinearLayout newItemLayout = new LinearLayout(this);
        newItemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newItemLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView numberText = new TextView(this);
        numberText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        numberText.setText(String.format("%d. ", ++allergenCounter));
        numberText.setTextSize(20);

        TextView allergenText = new TextView(this);
        allergenText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f));
        allergenText.setText(allergenName);
        allergenText.setTextSize(20);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        checkBox.setChecked(isChecked);

        newItemLayout.addView(numberText);
        newItemLayout.addView(allergenText);
        newItemLayout.addView(checkBox);

        layout2.addView(newItemLayout);
    }

    private boolean checkForDuplicate(String allergenName) {
        for (int i = 0; i < layout2.getChildCount(); i++) {
            LinearLayout allergenLayout = (LinearLayout) layout2.getChildAt(i);
            TextView allergenText = (TextView) allergenLayout.getChildAt(1);

            if (allergenText.getText().toString().equalsIgnoreCase(allergenName)) {
                return true;
            }
        }
        return false;
    }

    private void saveAllergensToPreferences() {
        Set<String> primaryAllergensSet = new HashSet<>();
        Set<String> allergensWithSynonymsSet = new HashSet<>();

        for (int i = 0; i < layout2.getChildCount(); i++) {
            LinearLayout allergenLayout = (LinearLayout) layout2.getChildAt(i);
            TextView allergenText = (TextView) allergenLayout.getChildAt(1);
            CheckBox checkBox = (CheckBox) allergenLayout.getChildAt(2);

            if (checkBox.isChecked()) {
                String allergen = allergenText.getText().toString();
                primaryAllergensSet.add(allergen);

                allergensWithSynonymsSet.add(allergen);
                if (allergenSynonyms.containsKey(allergen)) {
                    allergensWithSynonymsSet.addAll(allergenSynonyms.get(allergen));
                }
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("PrimaryAllergenList", primaryAllergensSet);
        editor.putStringSet("AllergenList", allergensWithSynonymsSet);
        editor.apply();
    }

    private void loadAllergensFromPreferences() {
        layout2.removeAllViews();
        allergenCounter = 0; // Reset counter

        // Load saved primary allergens for display
        Set<String> savedPrimaryAllergens = sharedPreferences.getStringSet("PrimaryAllergenList", new HashSet<>());

        // Display all saved primary allergens
        for (String allergen : savedPrimaryAllergens) {
            addAllergen(allergen, true);
        }
    }

    private void showInformationDialog() {
        final Dialog dialog = new Dialog(this);
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
        infoText.setText("1. Select your allergens.\n2. Insert allergens not in the list.\n3. Make sure to click save.");

        dialog.show();
    }
}