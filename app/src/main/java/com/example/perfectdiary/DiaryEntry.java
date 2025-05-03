package com.example.perfectdiary;

import java.util.Date;

public class DiaryEntry {
    private long id;
    private String title;
    private String content;
    private String dateTime;
    private String imagePath;
    private int backgroundColor;
    private String emotion;
    private Date date;

    // 默认构造函数
    public DiaryEntry() {
    }

    // 带所有字段的构造函数
    public DiaryEntry(long id, String title, String content, String dateTime,
                      String imagePath, int backgroundColor, String emotion) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateTime = dateTime;
        this.imagePath = imagePath;
        this.backgroundColor = backgroundColor;
        this.emotion = emotion;
    }

    // 所有必要的 getter 和 setter 方法
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DiaryEntry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", backgroundColor=" + backgroundColor +
                ", emotion='" + emotion + '\'' +
                ", date=" + date +
                '}';
    }
}