package com.example.final_fyp_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class allergenSynonym extends AppCompatActivity {

    private LinearLayout layoutForSynonyms;
    private SharedPreferences sharedPreferences;
    private final HashMap<String, List<String>> allergenSynonyms = new HashMap<String, List<String>>() {{
        put("Milk", Arrays.asList("Cheese", "Butter"));
        put("Egg", Arrays.asList("Yolk", "Egg White", "Egg Yolk", "Albumen"));
        put("Soy", Arrays.asList("Soybean", "Soya", "Soyabean", "Soya Bean"));
        put("Wheat", Arrays.asList("Gluten", "Barley"));
        put("Tree Nut", Arrays.asList("Almonds", "Brazil nuts", "Cashews", "Hazelnuts", "Macadamia nuts",
                "Pecans", "Pistachios", "Walnuts", "Pine nuts", "Hickory nuts"));
        put("Shellfish", Arrays.asList("Barnacle", "Crab", "Crawfish", "Krill", "Lobster", "Prawns", "Shrimp",
                "Abalone", "Clams", "Cockle", "Cuttlefish", "Limpet", "Mussels",
                "Octopus", "Oysters", "Periwinkle", "Scallops"));
        put("Fish", Arrays.asList("Surimi"));
        put("Peanut", new ArrayList<>()); // No synonyms
    }};

    private EditText inputNewAllergen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergen_synonym);

        sharedPreferences = getSharedPreferences("AllergenData", MODE_PRIVATE);
        layoutForSynonyms = findViewById(R.id.layout2);
        inputNewAllergen = findViewById(R.id.input_field);

        // Handle back button click
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> navigateToMainActivity());

        // Handle insert button click
        Button insertButton = findViewById(R.id.buttonInsert);
        insertButton.setOnClickListener(view -> {
            String allergenName = inputNewAllergen.getText().toString().trim();
            if (!allergenName.isEmpty()) {
                if (checkForDuplicate(allergenName)) {
                    Toast.makeText(allergenSynonym.this, "Allergen already exists in the list.", Toast.LENGTH_SHORT).show();
                } else {
                    addNewAllergen(allergenName); // Add allergen to UI
                    inputNewAllergen.setText(""); // Clear the input field
                    Toast.makeText(allergenSynonym.this, "Allergen added to the list. Click 'Save' to save changes.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(allergenSynonym.this, "Please enter an allergen.", Toast.LENGTH_SHORT).show();
            }
        });


        // Handle help button click
        ImageButton helpButton = findViewById(R.id.btnHelp);
        helpButton.setOnClickListener(v -> showHelpDialog());

        // Handle save button click
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(v -> saveAllergenData());

        // Handle reset button click
        Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(v -> resetAllergenData());

        displayAllergensWithSynonyms();
    }

    private boolean checkForDuplicate(String allergenName) {
        for (int i = 0; i < layoutForSynonyms.getChildCount(); i++) {
            View childView = layoutForSynonyms.getChildAt(i);
            if (childView instanceof LinearLayout) {
                LinearLayout allergenLayout = (LinearLayout) childView;
                View allergenView = allergenLayout.getChildAt(0);
                if (allergenView instanceof LinearLayout) {
                    LinearLayout allergenWithCheckBox = (LinearLayout) allergenView;
                    TextView allergenText = (TextView) allergenWithCheckBox.getChildAt(0);
                    String existingAllergenName = allergenText.getText().toString();
                    int firstDotIndex = existingAllergenName.indexOf(". ");
                    existingAllergenName = existingAllergenName.substring(firstDotIndex + 2).trim();
                    if (existingAllergenName.equalsIgnoreCase(allergenName)) {
                        return true; // Duplicate found
                    }
                }
            }
        }
        return false; // No duplicates
    }

    // Override the back button behavior to navigate to MainActivity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToMainActivity();
    }

    // Navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(allergenSynonym.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void displayAllergensWithSynonyms() {
        Set<String> savedPrimaryAllergens = sharedPreferences.getStringSet("PrimaryAllergenList", new HashSet<>());
        if (savedPrimaryAllergens == null || savedPrimaryAllergens.isEmpty()) {
            TextView noDataMessage = new TextView(this);
            noDataMessage.setText("No allergen data found.");
            layoutForSynonyms.addView(noDataMessage);
            return;
        }

        int index = 1; // Numbering for allergens
        for (String allergen : savedPrimaryAllergens) {
            LinearLayout allergenLayout = new LinearLayout(this);
            allergenLayout.setOrientation(LinearLayout.VERTICAL);
            allergenLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            // Create a horizontal layout for allergen name and checkbox
            LinearLayout allergenWithCheckBox = new LinearLayout(this);
            allergenWithCheckBox.setOrientation(LinearLayout.HORIZONTAL);
            allergenWithCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            // Add allergen name with numbering
            TextView allergenText = new TextView(this);
            allergenText.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f));
            allergenText.setText(index + ". " + allergen); // Numbered allergen
            allergenText.setTextSize(20);
            allergenWithCheckBox.addView(allergenText);

            // Add a checkbox for the allergen
            CheckBox allergenCheckBox = new CheckBox(this);
            allergenCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // Set the checkbox checked state based on saved data
            boolean isChecked = sharedPreferences.getBoolean(allergen, true); // Default to checked
            allergenCheckBox.setChecked(isChecked);
            allergenWithCheckBox.addView(allergenCheckBox);

            allergenLayout.addView(allergenWithCheckBox);

            // Add synonyms if available (skip if no synonyms)
            if (allergenSynonyms.containsKey(allergen) && !allergenSynonyms.get(allergen).isEmpty()) {
                LinearLayout synonymLayout = new LinearLayout(this);
                synonymLayout.setOrientation(LinearLayout.VERTICAL);
                synonymLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // Handle special case for "Shellfish"
                if (allergen.equals("Shellfish")) {
                    // Display Crustaceans and Mollusks categories
                    String crustaceans = "Crustaceans: Barnacle, Crab, Crawfish, Krill, Lobster, Prawn, Shrimp";
                    String mollusks = "Mollusks: Abalone, Clam, Cockle, Cuttlefish, Limpet, Mussel, Octopus, Oyster, Periwinkle, Scallop";

                    TextView crustaceansText = new TextView(this);
                    crustaceansText.setText("- " + crustaceans);
                    crustaceansText.setTextSize(14);
                    synonymLayout.addView(crustaceansText);

                    TextView mollusksText = new TextView(this);
                    mollusksText.setText("- " + mollusks);
                    mollusksText.setTextSize(14);
                    synonymLayout.addView(mollusksText);
                } else {
                    // For other allergens, list all synonyms
                    String synonyms = String.join(", ", allergenSynonyms.get(allergen));
                    TextView synonymsText = new TextView(this);
                    synonymsText.setText("- " + synonyms);
                    synonymsText.setTextSize(14);
                    synonymLayout.addView(synonymsText);
                }

                allergenLayout.addView(synonymLayout);
            }

            // Add the allergen layout to the main layout
            layoutForSynonyms.addView(allergenLayout);
            index++; // Increment index for numbering
        }
    }

    // Add a new allergen with its checkbox state
    private void addNewAllergen(String allergenName) {
        if (checkForDuplicate(allergenName)) return;

        String newAllergen = inputNewAllergen.getText().toString().trim();

        if (newAllergen.isEmpty()) {
            Toast.makeText(this, "Please enter an allergen.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current number of allergens in the layout
        int currentAllergenCount = layoutForSynonyms.getChildCount() + 1;

        // Add the new allergen to the UI only (does not persist yet)
        LinearLayout allergenLayout = new LinearLayout(this);
        allergenLayout.setOrientation(LinearLayout.VERTICAL);

        // Create a horizontal layout for allergen name and checkbox
        LinearLayout allergenWithCheckBox = new LinearLayout(this);
        allergenWithCheckBox.setOrientation(LinearLayout.HORIZONTAL);

        // Add allergen name with numbering
        TextView allergenText = new TextView(this);
        allergenText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f));
        allergenText.setText(currentAllergenCount + ". " + newAllergen); // Include numbering
        allergenText.setTextSize(20);
        allergenWithCheckBox.addView(allergenText);

        // Add a checkbox for the allergen
        CheckBox allergenCheckBox = new CheckBox(this);
        allergenCheckBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        allergenCheckBox.setChecked(true); // Default checkbox state
        allergenWithCheckBox.addView(allergenCheckBox);

        allergenLayout.addView(allergenWithCheckBox);

        // Add the allergen layout to the main layout
        layoutForSynonyms.addView(allergenLayout);

        // Clear input field
        inputNewAllergen.setText("");

        Toast.makeText(this, "Allergen added to the list. Click 'Save' to save changes.", Toast.LENGTH_SHORT).show();
    }

    private void saveAllergenData() {
        // Save all allergens and their states to SharedPreferences
        Set<String> updatedAllergens = new HashSet<>();

        // Iterate through child views in layoutForSynonyms
        for (int i = 0; i < layoutForSynonyms.getChildCount(); i++) {
            View childView = layoutForSynonyms.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout allergenLayout = (LinearLayout) childView;
                View allergenView = allergenLayout.getChildAt(0);

                if (allergenView instanceof LinearLayout) {
                    LinearLayout allergenWithCheckBox = (LinearLayout) allergenView;

                    // Get allergen name
                    TextView allergenText = (TextView) allergenWithCheckBox.getChildAt(0);
                    String allergenNameWithNumber = allergenText.getText().toString();

                    // Remove numbering using substring
                    int firstDotIndex = allergenNameWithNumber.indexOf(". ");
                    String allergenName = allergenNameWithNumber.substring(firstDotIndex + 2); // Extract allergen name

                    // Get checkbox state
                    CheckBox allergenCheckBox = (CheckBox) allergenWithCheckBox.getChildAt(1);
                    boolean isChecked = allergenCheckBox.isChecked();

                    // Save to SharedPreferences
                    updatedAllergens.add(allergenName);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(allergenName, isChecked);
                    editor.apply();
                }
            }
        }

        // Save the updated allergen list
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("PrimaryAllergenList", updatedAllergens);
        editor.apply();

        // Refresh the allergen list
        layoutForSynonyms.removeAllViews();
        displayAllergensWithSynonyms();
        Toast.makeText(this, "Allergen data saved successfully.", Toast.LENGTH_SHORT).show();
    }

    // Utility method to generate unique checkbox ID based on allergen
    private int getCheckboxId(String allergen) {
        return allergen.hashCode(); // Return a unique ID based on allergen name
    }

    private void resetAllergenData() {
        // Reset allergen data by clearing saved preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("PrimaryAllergenList");
        editor.apply();

        // Clear the current UI
        layoutForSynonyms.removeAllViews();

        // Show a toast message
        Toast.makeText(this, "Allergen data has been reset.", Toast.LENGTH_SHORT).show();

        // Navigate back to the Profile Activity (or home screen)
        Intent intent = new Intent(this, profile.class);  // Replace ProfileActivity with your actual profile page class
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // This clears the activity stack
        startActivity(intent);
        finish();  // Finish the current activity to remove it from the stack
    }

    private void showHelpDialog() {
        // Show a simple Toast or Dialog to provide help information
        Toast.makeText(this, "This screen displays allergen synonyms.", Toast.LENGTH_SHORT).show();
    }
}
