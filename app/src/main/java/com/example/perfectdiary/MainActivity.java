package com.example.perfectdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private DiaryEntryAdapter adapter;
    private List<DiaryEntry> diaryEntries;
    private RecyclerView diaryListView;
    private TextView greetingTextView;
    private TextView dateTextView;
    private BottomNavigationView bottomNavigationView;
    private TabLayout tabLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE);

        // Apply the saved theme before setContentView()
        String savedTheme = sharedPreferences.getString("theme", "Light");
        applyTheme(savedTheme);

        setContentView(R.layout.activity_main);

        // Initialize database helper
        String username = sharedPreferences.getString("loggedInUser", "default");
        databaseHelper = new DatabaseHelper(this, username);
        Log.d("MainActivity", "Database path: " + databaseHelper.getReadableDatabase().getPath());

        // Initialize views
        greetingTextView = findViewById(R.id.greetingTextView);
        dateTextView = findViewById(R.id.dateTextView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tabLayout = findViewById(R.id.tabLayout);

        // Setup RecyclerView
        diaryListView = findViewById(R.id.diaryListView);
        diaryEntries = new ArrayList<>();
        adapter = new DiaryEntryAdapter(this, diaryEntries);
        diaryListView.setLayoutManager(new LinearLayoutManager(this));
        diaryListView.setAdapter(adapter);

        // 添加条目点击事件
        diaryListView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    if (position != RecyclerView.NO_POSITION) {
                        DiaryEntry selectedEntry = diaryEntries.get(position);
                        Intent intent = new Intent(MainActivity.this, Add.class);
                        intent.putExtra("ENTRY_ID", selectedEntry.getId());
                        intent.putExtra("ENTRY_TITLE", selectedEntry.getTitle());
                        intent.putExtra("ENTRY_CONTENT", selectedEntry.getContent());
                        intent.putExtra("ENTRY_DATETIME", selectedEntry.getDateTime());
                        intent.putExtra("ENTRY_IMAGE_PATH", selectedEntry.getImagePath());
                        intent.putExtra("ENTRY_BACKGROUND_COLOR", selectedEntry.getBackgroundColor());
                        intent.putExtra("ENTRY_EMOTION", selectedEntry.getEmotion());
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        // Set greeting
        String username_display = sharedPreferences.getString("loggedInUser", "User");
        greetingTextView.setText("Hello " + username_display + ", Welcome!");

        // Load entries from database
        loadDiaryEntries();

        // Set up Bottom Navigation View listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getOrder()) {
                    case 0: // Home
                        Toast.makeText(MainActivity.this, "Home Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case 1: // Search
                        Toast.makeText(MainActivity.this, "Navigating to Search", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        return true;

                    case 2: // Add
                        intent = new Intent(MainActivity.this, Add.class);
                        startActivity(intent);
                        return true;
                    case 3: // Emotion
                        intent = new Intent(MainActivity.this, EmotionalAnalysisActivity.class);
                        startActivity(intent);
                        return true;
                    case 4: // Profile
                        intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Set up Tab Layout listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    Toast.makeText(MainActivity.this, "All Entries Selected", Toast.LENGTH_SHORT).show();
                } else if (tab.getPosition() == 1) {
                    Toast.makeText(MainActivity.this, "Favourites Selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadDiaryEntries() {
        Log.d("MainActivity", "Loading diary entries...");
        diaryEntries.clear();
        List<DiaryEntry> entries = databaseHelper.getAllDiaryEntries();

        // 添加调试日志来检查背景颜色
        for (DiaryEntry entry : entries) {
            Log.d("MainActivity", "Entry: " + entry.getTitle() +
                    ", Background Color: " + entry.getBackgroundColor());
        }

        diaryEntries.addAll(entries);
        adapter.notifyDataSetChanged();
    }

    private void applyTheme(String theme) {
        Log.d("MainActivity", "Applying theme: " + theme);
        if (theme.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在恢复时应用主题
        String savedTheme = sharedPreferences.getString("theme", "Light");
        applyTheme(savedTheme);

        // 更新用户名
        String username = sharedPreferences.getString("loggedInUser", "User");
        greetingTextView.setText("Hello " + username + ", Welcome!");

        // 刷新条目
        loadDiaryEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}