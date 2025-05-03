package com.example.perfectdiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Add extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText;
    private EditText contentEditText;
    private TextView dateTimeTextView;
    private ImageButton fontButton;
    private ImageButton attachImageButton;
    private ImageButton themeButton;
    private ImageView attachedImageView;
    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private View rootLayout;
    private ImageButton backButton;
    private String selectedEmotion = "Neutral";
    private ImageButton emotionButton;
    private long entryId = -1;

    // 字体样式相关
    private int currentFontStyleIndex = 0;
    private int[] fontStyles = {Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC};

    // 背景颜色相关
    private int[] backgroundColors = {
            R.color.light_pink,
            R.color.light_blue,
            R.color.light_yellow,
            R.color.light_red,
            R.color.light_gray
    };
    private int currentColorIndex = 0;
    private int selectedBackgroundColor = R.color.light_gray;
    private SharedPreferences sharedPreferences;

    // 其他变量
    private Calendar selectedCalendar;
    private Uri selectedImageUri;
    private String savedImagePath;
    private static final int EMOTION_SELECTION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE);

        // 初始化数据库帮助类
        currentUsername = sharedPreferences.getString("loggedInUser", "default_user");
        databaseHelper = new DatabaseHelper(this, currentUsername);

        // 初始化 UI 组件
        rootLayout = findViewById(R.id.rootLayout);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        fontButton = findViewById(R.id.fontButton);
        attachImageButton = findViewById(R.id.attachImageButton);
        themeButton = findViewById(R.id.themeButton);
        ImageButton saveButton = findViewById(R.id.saveButton);
        attachedImageView = findViewById(R.id.attachedImageView);
        backButton = findViewById(R.id.imageButton);
        emotionButton = findViewById(R.id.emotionButton);
        selectedCalendar = Calendar.getInstance();

        // 设置情绪按钮点击事件
        emotionButton.setOnClickListener(v -> showEmotionSelectionDialog());

        // 处理返回按钮点击事件
        backButton.setOnClickListener(v -> {
            if (hasUnsavedChanges()) {
                showUnsavedChangesDialog();
            } else {
                navigateToMainActivity();
            }
        });

        // 检查是否是编辑已存在的条目
        Intent intent = getIntent();
        entryId = intent.getLongExtra("ENTRY_ID", -1);

        if (entryId != -1) {
            // 如果是编辑已存在的条目，显示删除按钮
            ImageButton deleteButton = findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> deleteEntry());

            // 填充已存在的条目信息
            String title = intent.getStringExtra("ENTRY_TITLE");
            String content = intent.getStringExtra("ENTRY_CONTENT");
            String dateTime = intent.getStringExtra("ENTRY_DATETIME");
            String imagePath = intent.getStringExtra("ENTRY_IMAGE_PATH");
            int backgroundColor = intent.getIntExtra("ENTRY_BACKGROUND_COLOR", R.color.light_gray);
            String emotion = intent.getStringExtra("ENTRY_EMOTION");

            titleEditText.setText(title);
            contentEditText.setText(content);
            dateTimeTextView.setText(dateTime);
            selectedBackgroundColor = backgroundColor;
            selectedEmotion = emotion != null ? emotion : "Neutral";

            // 设置背景颜色
            rootLayout.setBackgroundColor(ContextCompat.getColor(this, selectedBackgroundColor));

            // 如果有图片，加载图片
            if (imagePath != null && !imagePath.isEmpty()) {
                loadImage(imagePath);
            }
        } else {
            // 新建条目时设置当前日期
            setCurrentDateTime();
        }

        // 主题按钮 - 更改背景颜色
        themeButton.setOnClickListener(v -> changeBackgroundColor());

        // 字体变更按钮
        fontButton.setOnClickListener(v -> changeFontStyle());

        // 图片附件按钮
        attachImageButton.setOnClickListener(v -> openImagePicker());

        // 保存按钮
        saveButton.setOnClickListener(v -> saveEntry());

        // 限制日期选择
        limitDateSelection();
    }

    // 检查是否有未保存的更改
    private boolean hasUnsavedChanges() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        return !title.isEmpty() || !content.isEmpty() || selectedImageUri != null;
    }

    // 显示未保存更改的对话框
    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved content. Are you sure you want to go back?")
                .setPositiveButton("Confirm", (dialog, which) -> navigateToMainActivity())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // 导航到主活动
    private void navigateToMainActivity() {
        Intent intent = new Intent(Add.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 更改背景颜色
    private void changeBackgroundColor() {
        currentColorIndex = (currentColorIndex + 1) % backgroundColors.length;
        selectedBackgroundColor = backgroundColors[currentColorIndex];

        // 更新背景颜色
        rootLayout.setBackgroundColor(ContextCompat.getColor(this, selectedBackgroundColor));

        // 可选：保存当前背景颜色到 SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_background_color", selectedBackgroundColor);
        editor.apply();
    }

    // 设置当前日期
    private void setCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTimeTextView.setText(currentDate);
    }

    // 限制日期选择
    private void limitDateSelection() {
        dateTimeTextView.setOnClickListener(v -> showDatePickerDialog());
    }

    // 显示日期选择对话框
    private void showDatePickerDialog() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String selectedDate = sdf.format(selectedCalendar.getTime());
                    dateTimeTextView.setText(selectedDate);
                },
                year, month, day
        );

        // 限制最大日期为当前日期
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    // 更改字体样式
    private void changeFontStyle() {
        currentFontStyleIndex = (currentFontStyleIndex + 1) % fontStyles.length;

        Typeface typeface;
        switch (fontStyles[currentFontStyleIndex]) {
            case Typeface.BOLD:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
                break;
            case Typeface.ITALIC:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
                break;
            default:
                typeface = Typeface.DEFAULT;
        }

        titleEditText.setTypeface(typeface);
        contentEditText.setTypeface(typeface);
    }

    // 打开图片选择器
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 显示情绪选择对话框
    private void showEmotionSelectionDialog() {
        final String[] emotions = {"Happy", "Sad", "Angry", "Anxious", "Neutral"};

        new AlertDialog.Builder(this)
                .setTitle("Select Your Emotion")
                .setSingleChoiceItems(emotions, -1, (dialog, which) -> {
                    selectedEmotion = emotions[which];
                    Toast.makeText(Add.this, "Selected: " + selectedEmotion, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    attachedImageView.setImageBitmap(bitmap);
                    attachedImageView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 加载图片
    private void loadImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(imageFile));
                attachedImageView.setImageBitmap(bitmap);
                attachedImageView.setVisibility(View.VISIBLE);
                savedImagePath = imagePath;
            } else {
                attachedImageView.setVisibility(View.GONE);
                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            attachedImageView.setVisibility(View.GONE);
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    // 将图片保存到内部存储
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            File storageDir = getFilesDir();
            File imageFile = File.createTempFile(
                    imageFileName,  /* 前缀 */
                    ".jpg",         /* 后缀 */
                    storageDir      /* 目录 */
            );

            try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
                 OutputStream outputStream = new FileOutputStream(imageFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 保存条目
    private void saveEntry() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String date = dateTimeTextView.getText().toString();

        // 检查标题和内容是否为空
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        // 在保存前保存图片
        if (selectedImageUri != null) {
            savedImagePath = saveImageToInternalStorage(selectedImageUri);
        }

        // 检查是新建条目还是更新已存在的条目
        if (entryId == -1) {
            // 保存新条目到数据库
            long result = databaseHelper.insertDiaryEntry(
                    title,
                    content,
                    date,
                    savedImagePath,
                    selectedBackgroundColor,
                    selectedEmotion
            );

            Log.d("Add", "Insert result: " + result);

            if (result != -1) {
                // 导航到主活动
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 更新已存在的条目
            DiaryEntry entry = new DiaryEntry();
            entry.setId(entryId);
            entry.setTitle(title);
            entry.setContent(content);
            entry.setDateTime(date);
            entry.setImagePath(savedImagePath);
            entry.setBackgroundColor(selectedBackgroundColor);
            entry.setEmotion(selectedEmotion);

            boolean result = databaseHelper.updateDiaryEntry(entry);
            if (result) {
                // 导航到主活动
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to update entry", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 删除条目
    private void deleteEntry() {
        if (entryId != -1) {
            // 从数据库删除条目
            databaseHelper.deleteDiaryEntry(entryId);

            // 返回到主活动
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}