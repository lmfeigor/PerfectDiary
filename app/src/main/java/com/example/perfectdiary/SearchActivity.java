package com.example.perfectdiary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryEntryAdapter adapter;
    private List<DiaryEntry> searchResults;
    private EditText searchEditText, dateFromEditText, dateToEditText;
    private Button searchButton, backToHomeButton;
    private Spinner emotionSpinner;
    private int year, month, day;

    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Retrieve the logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("loggedInUser", "default");

        // Initialize DatabaseHelper with username from SharedPreferences
        databaseHelper = new DatabaseHelper(this, loggedInUsername);
        Log.d("SearchActivity", "DatabaseHelper initialized with username: " + loggedInUsername);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        searchEditText = findViewById(R.id.searchKeywordEditText);
        searchButton = findViewById(R.id.searchButton);
        emotionSpinner = findViewById(R.id.emotionSpinner);
        dateFromEditText = findViewById(R.id.dateFromEditText);
        dateToEditText = findViewById(R.id.dateToEditText);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize the search results list
        searchResults = new ArrayList<>();

        // Initialize the adapter for RecyclerView
        adapter = new DiaryEntryAdapter(this, searchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Populate emotion spinner
        ArrayAdapter<CharSequence> emotionAdapter = ArrayAdapter.createFromResource(this,
                R.array.emotions_array, android.R.layout.simple_spinner_item);
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotionSpinner.setAdapter(emotionAdapter);

        // Set DatePicker for "From" Date
        dateFromEditText.setOnClickListener(v -> showDatePickerDialog("from"));

        // Set DatePicker for "To" Date
        dateToEditText.setOnClickListener(v -> showDatePickerDialog("to"));

        // Handle search button click
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            String emotion = emotionSpinner.getSelectedItem() != null ? emotionSpinner.getSelectedItem().toString() : "";
            String dateFrom = dateFromEditText.getText().toString().trim();
            String dateTo = dateToEditText.getText().toString().trim();

            if (!query.isEmpty() || !emotion.isEmpty() || !dateFrom.isEmpty() || !dateTo.isEmpty()) {
                performSearch(query, emotion, dateFrom, dateTo, databaseHelper);
            } else {
                Toast.makeText(SearchActivity.this, "Please enter search criteria", Toast.LENGTH_SHORT).show();
            }
        });

        backToHomeButton.setOnClickListener(v -> {
            // Navigate to Home (MainActivity)
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getOrder()) {
                    case 0: // Home
                        intent = new Intent(SearchActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case 1: // Search
                        Toast.makeText(SearchActivity.this, "Navigating to Search", Toast.LENGTH_SHORT).show();
                        return true;

                    case 2: // Add
                        intent = new Intent(SearchActivity.this, Add.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case 3: // Emotion
                        intent = new Intent(SearchActivity.this, EmotionalAnalysisActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case 4: // Profile
                        intent = new Intent(SearchActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void performSearch(String query, String emotion, String dateFrom, String dateTo, DatabaseHelper databaseHelper) {
        Log.d("SearchActivity", "Performing search for query: " + query);

        // Use the DatabaseHelper to search for diary entries based on the query, date range, and emotion
        List<DiaryEntry> entries = databaseHelper.searchDiaryEntries(query, emotion, dateFrom, dateTo);
        updateSearchResults(entries); // Use a method to update the adapter's data
        Log.d("SearchActivity", "Search completed, " + entries.size() + " results found");
    }

    private void updateSearchResults(List<DiaryEntry> newEntries) {
        searchResults.clear();
        searchResults.addAll(newEntries);
        adapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(String dateType) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            String selectedDate = formatDate(selectedYear, selectedMonth + 1, selectedDayOfMonth);

            if (dateType.equals("from")) {
                dateFromEditText.setText(selectedDate);
            } else if (dateType.equals("to")) {
                dateToEditText.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private String formatDate(int year, int month, int day) {
        // Format the date into dd MMM yyyy format
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Month is 0-indexed
        return sdf.format(calendar.getTime());
    }
}

