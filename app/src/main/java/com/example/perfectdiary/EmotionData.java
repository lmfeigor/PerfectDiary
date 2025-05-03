// EmotionData.java
package com.example.perfectdiary;

public class EmotionData {
    private String emotion;
    private float percentage;

    public EmotionData(String emotion, float percentage) {
        this.emotion = emotion;
        this.percentage = percentage;
    }

    // Getters and setters
    public String getEmotion() { return emotion; }
    public float getPercentage() { return percentage; }
}