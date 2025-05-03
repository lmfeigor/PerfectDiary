package com.example.perfectdiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_DIARY = "diary_entries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DATETIME = "datetime";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_BACKGROUND_COLOR = "background_color";
    private static final String COLUMN_EMOTION = "emotion";

    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";

    private static final String CREATE_DIARY_TABLE =
            "CREATE TABLE " + TABLE_DIARY + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_CONTENT + " TEXT,"
                    + COLUMN_DATETIME + " TEXT,"
                    + COLUMN_IMAGE_PATH + " TEXT,"
                    + COLUMN_BACKGROUND_COLOR + " INTEGER DEFAULT " + R.color.light_gray + ","
                    + COLUMN_EMOTION + " TEXT DEFAULT 'Neutral'"
                    + ")";

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + " ("
                    + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USER_NAME + " TEXT UNIQUE, "
                    + COLUMN_USER_PASSWORD + " TEXT"
                    + ")";

    public DatabaseHelper(Context context, String username) {
        super(context, username + "_PerfectDiary.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIARY_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        Log.d("DatabaseHelper", "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {
            try {
                boolean backgroundColorColumnExists = false;
                boolean emotionColumnExists = false;

                Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_DIARY + ")", null);

                if (cursor != null) {
                    int nameColumnIndex = cursor.getColumnIndex("name");

                    while (cursor.moveToNext()) {
                        String columnName = nameColumnIndex != -1 ?
                                cursor.getString(nameColumnIndex) : null;

                        if (columnName != null) {
                            if (COLUMN_BACKGROUND_COLOR.equals(columnName)) {
                                backgroundColorColumnExists = true;
                            }
                            if (COLUMN_EMOTION.equals(columnName)) {
                                emotionColumnExists = true;
                            }
                        }
                    }
                    cursor.close();
                }

                if (!backgroundColorColumnExists) {
                    db.execSQL("ALTER TABLE " + TABLE_DIARY +
                            " ADD COLUMN " + COLUMN_BACKGROUND_COLOR +
                            " INTEGER DEFAULT " + R.color.light_gray);
                }

                if (!emotionColumnExists) {
                    db.execSQL("ALTER TABLE " + TABLE_DIARY +
                            " ADD COLUMN " + COLUMN_EMOTION +
                            " TEXT DEFAULT 'Neutral'");
                }
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error during database column upgrade", e);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
                onCreate(db);
            }
        }
    }

    public long insertDiaryEntry(String title, String content, String dateTime,
                                 String imagePath, int backgroundColor, String emotion) {
        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_CONTENT, content);
            values.put(COLUMN_DATETIME, dateTime);
            values.put(COLUMN_IMAGE_PATH, imagePath);
            values.put(COLUMN_BACKGROUND_COLOR, backgroundColor);
            values.put(COLUMN_EMOTION, emotion);

            id = db.insert(TABLE_DIARY, null, values);

            Log.d("DatabaseHelper", "Inserted diary entry: " +
                    "\nTitle: " + title +
                    "\nDate: " + dateTime +
                    "\nEmotion: " + emotion +
                    "\nBackground Color: " + backgroundColor +
                    "\nID: " + id);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting diary entry", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return id;
    }

    public long insertDiaryEntry(String title, String content, String dateTime, String imagePath) {
        return insertDiaryEntry(title, content, dateTime, imagePath,
                R.color.light_gray, "Neutral");
    }

    public List<DiaryEntry> getAllDiaryEntries() {
        List<DiaryEntry> entryList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + TABLE_DIARY + " ORDER BY " + COLUMN_ID + " DESC";
            db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int dateTimeIndex = cursor.getColumnIndex(COLUMN_DATETIME);
            int imagePathIndex = cursor.getColumnIndex(COLUMN_IMAGE_PATH);
            int backgroundColorIndex = cursor.getColumnIndex(COLUMN_BACKGROUND_COLOR);
            int emotionIndex = cursor.getColumnIndex(COLUMN_EMOTION);

            if (cursor.moveToFirst()) {
                do {
                    DiaryEntry entry = new DiaryEntry();

                    if (idIndex != -1) entry.setId(cursor.getInt(idIndex));
                    if (titleIndex != -1) entry.setTitle(cursor.getString(titleIndex));
                    if (contentIndex != -1) entry.setContent(cursor.getString(contentIndex));
                    if (dateTimeIndex != -1) entry.setDateTime(cursor.getString(dateTimeIndex));
                    if (imagePathIndex != -1) entry.setImagePath(cursor.getString(imagePathIndex));
                    if (backgroundColorIndex != -1) entry.setBackgroundColor(cursor.getInt(backgroundColorIndex));
                    if (emotionIndex != -1) entry.setEmotion(cursor.getString(emotionIndex));

                    entryList.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return entryList;
    }

    public boolean updateDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = null;
        boolean isUpdated = false;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, entry.getTitle());
            values.put(COLUMN_CONTENT, entry.getContent());
            values.put(COLUMN_DATETIME, entry.getDateTime());
            values.put(COLUMN_IMAGE_PATH, entry.getImagePath());
            values.put(COLUMN_BACKGROUND_COLOR, entry.getBackgroundColor());
            values.put(COLUMN_EMOTION, entry.getEmotion());

            int rowsAffected = db.update(TABLE_DIARY, values,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(entry.getId())});

            isUpdated = rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return isUpdated;
    }

    public boolean deleteDiaryEntry(long entryId) {
        SQLiteDatabase db = null;
        boolean isDeleted = false;
        try {
            db = this.getWritableDatabase();
            int rowsAffected = db.delete(TABLE_DIARY,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(entryId)});
            isDeleted = rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return isDeleted;
    }

    public List<DiaryEntry> searchDiaryEntries(String keyword, String emotion,
                                               String dateFrom, String dateTo) {
        List<DiaryEntry> entryList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            StringBuilder selectQuery = new StringBuilder(
                    "SELECT * FROM " + TABLE_DIARY + " WHERE ("
                            + COLUMN_TITLE + " LIKE ? OR " + COLUMN_CONTENT + " LIKE ?)");

            if (emotion != null && !emotion.isEmpty() && !emotion.equals("All")) {
                selectQuery.append(" AND " + COLUMN_EMOTION + " = ?");
            }

            if (dateFrom != null && !dateFrom.isEmpty() &&
                    dateTo != null && !dateTo.isEmpty()) {
                selectQuery.append(" AND " + COLUMN_DATETIME + " BETWEEN ? AND ?");
            }

            if (emotion != null && !emotion.isEmpty() && !emotion.equals("All") &&
                    dateFrom != null && !dateFrom.isEmpty() &&
                    dateTo != null && !dateTo.isEmpty()) {
                cursor = db.rawQuery(selectQuery.toString(),
                        new String[]{"%" + keyword + "%", "%" + keyword + "%",
                                emotion, dateFrom, dateTo});
            } else if (emotion != null && !emotion.isEmpty() && !emotion.equals("All")) {
                cursor = db.rawQuery(selectQuery.toString(),
                        new String[]{"%" + keyword + "%", "%" + keyword + "%", emotion});
            } else if (dateFrom != null && !dateFrom.isEmpty() &&
                    dateTo != null && !dateTo.isEmpty()) {
                cursor = db.rawQuery(selectQuery.toString(),
                        new String[]{"%" + keyword + "%", "%" + keyword + "%",
                                dateFrom, dateTo});
            } else {
                cursor = db.rawQuery(selectQuery.toString(),
                        new String[]{"%" + keyword + "%", "%" + keyword + "%"});
            }

            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int dateTimeIndex = cursor.getColumnIndex(COLUMN_DATETIME);
            int imagePathIndex = cursor.getColumnIndex(COLUMN_IMAGE_PATH);
            int backgroundColorIndex = cursor.getColumnIndex(COLUMN_BACKGROUND_COLOR);
            int emotionIndex = cursor.getColumnIndex(COLUMN_EMOTION);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DiaryEntry entry = new DiaryEntry();

                    if (idIndex != -1) entry.setId(cursor.getInt(idIndex));
                    if (titleIndex != -1) entry.setTitle(cursor.getString(titleIndex));
                    if (contentIndex != -1) entry.setContent(cursor.getString(contentIndex));
                    if (dateTimeIndex != -1) entry.setDateTime(cursor.getString(dateTimeIndex));
                    if (imagePathIndex != -1) entry.setImagePath(cursor.getString(imagePathIndex));
                    if (backgroundColorIndex != -1) entry.setBackgroundColor(cursor.getInt(backgroundColorIndex));
                    if (emotionIndex != -1) entry.setEmotion(cursor.getString(emotionIndex));

                    entryList.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return entryList;
    }

    public List<String> getEmotionsForWeek(String startDate, String endDate) {
        List<String> emotions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT datetime, emotion FROM " + TABLE_DIARY, null);
            Log.d("DatabaseDebug", "Total entries in database: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd MMM yyyy", Locale.ENGLISH);
                int matchCount = 0;

                do {
                    String dbDateStr = cursor.getString(0);
                    String emotion = cursor.getString(1);

                    try {
                        Date dbDate = sdf.parse(dbDateStr);
                        Date start = sdf.parse(startDate);
                        Date end = sdf.parse(endDate);

                        if (dbDate.compareTo(start) >= 0 && dbDate.compareTo(end) <= 0) {
                            emotions.add(emotion);
                            matchCount++;
                            Log.d("DatabaseDebug", "WEEKLY MATCH: " + dbDateStr + " - " + emotion);
                        }
                    } catch (ParseException e) {
                        Log.e("DatabaseDebug", "Error parsing date: " + dbDateStr, e);
                    }
                } while (cursor.moveToNext());

                Log.d("DatabaseDebug", "Total weekly matches found: " + matchCount);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in getEmotionsForWeek", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return emotions;
    }

    public List<String> getEmotionsForMonth(int year,int month) {
        List<String> emotions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT datetime, emotion FROM " + TABLE_DIARY, null);
            Log.d("DatabaseDebug", "Total entries in database: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd MMM yyyy", Locale.ENGLISH);
                int matchCount = 0;

                do {
                    String dbDateStr = cursor.getString(0);
                    String emotion = cursor.getString(1);

                    try {
                        Date dbDate = sdf.parse(dbDateStr);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dbDate);
                        int entryYear = cal.get(Calendar.YEAR);
                        int entryMonth = cal.get(Calendar.MONTH);

                        if (entryYear == year && entryMonth == month - 1) {
                            emotions.add(emotion);
                            matchCount++;
                            Log.d("DatabaseDebug", "MONTHLY MATCH: " + dbDateStr + " - " + emotion);
                        }
                    } catch (ParseException e) {
                        Log.e("DatabaseDebug", "Error parsing date: " + dbDateStr, e);
                    }
                } while (cursor.moveToNext());

                Log.d("DatabaseDebug", "Total monthly matches found: " + matchCount);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in getEmotionsForMonth", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return emotions;
    }

    public List<String> getEmotionsForYear(int year) {
        List<String> emotions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT datetime, emotion FROM " + TABLE_DIARY, null);
            Log.d("DatabaseDebug", "Total entries in database: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd MMM yyyy", Locale.ENGLISH);
                int matchCount = 0;

                do {
                    String dbDateStr = cursor.getString(0);
                    String emotion = cursor.getString(1);

                    try {
                        Date dbDate = sdf.parse(dbDateStr);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dbDate);
                        int entryYear = cal.get(Calendar.YEAR);

                        if (entryYear == year) {
                            emotions.add(emotion);
                            matchCount++;
                            Log.d("DatabaseDebug", "YEARLY MATCH: " + dbDateStr + " - " + emotion);
                        }
                    } catch (ParseException e) {
                        Log.e("DatabaseDebug", "Error parsing date: " + dbDateStr, e);
                    }
                } while (cursor.moveToNext());

                Log.d("DatabaseDebug", "Total yearly matches found: " + matchCount);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in getEmotionsForYear", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return emotions;
    }

    public boolean addUser(String username, String password) {
        username = username.trim().toLowerCase();
        password = password.trim();

        if (checkUser(username)) {
            Log.d("DatabaseHelper", "Username already exists: " + username);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        db.close();

        if (result == -1) {
            Log.d("DatabaseHelper", "Failed to insert user: " + username);
            return false;
        } else {
            Log.d("DatabaseHelper", "User inserted: " + username);
            return true;
        }
    }

    public boolean checkUser(String username) {
        username = username.trim().toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USER, columns, selection, selectionArgs, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        Log.d("DatabaseHelper", "checkUser: " + username + " exists? " + exists);
        return exists;
    }

    public boolean checkUserLogin(String username, String password) {
        username = username.trim().toLowerCase();
        password = password.trim();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_NAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                TABLE_USER, columns, selection, selectionArgs, null, null, null);

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        db.close();

        Log.d("DatabaseHelper", "checkUserLogin: username=" + username + ", valid? " + valid);
        return valid;
    }

    public boolean updateUsername(String oldUsername, String newUsername) {
        oldUsername = oldUsername.trim().toLowerCase();
        newUsername = newUsername.trim().toLowerCase();

        if (checkUser(newUsername)) {
            Log.d("DatabaseHelper", "New username already exists: " + newUsername);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, newUsername);

        String selection = COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = {oldUsername};

        int count = db.update(TABLE_USER, values, selection, selectionArgs);
        db.close();

        Log.d("DatabaseHelper", "updateUsername: updated " + count +
                " rows for " + oldUsername + " to " + newUsername);
        return count > 0;
    }

    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        username = username.trim().toLowerCase();
        oldPassword = oldPassword.trim();
        newPassword = newPassword.trim();

        if (!checkUserLogin(username, oldPassword)) {
            Log.d("DatabaseHelper", "Incorrect old password for user: " + username);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, newPassword);

        String selection = COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = {username};

        int count = db.update(TABLE_USER, values, selection, selectionArgs);
        db.close();

        Log.d("DatabaseHelper", "updatePassword: updated " + count +
                " rows for user: " + username);
        return count > 0;
    }
}