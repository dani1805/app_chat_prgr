package com.example.projectchat.Models;

import com.google.gson.annotations.SerializedName;

public class NotificationData {

    String body;
    String title;
    @SerializedName("key_1")
    String firstKey;
    @SerializedName("key_2")
    String secondKey;

    public NotificationData(String body, String title, String firstKey) {
        this.body = body;
        this.title = title;
        this.firstKey = firstKey;
    }

    public NotificationData() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstKey() {
        return firstKey;
    }

    public void setFirstKey(String firstKey) {
        this.firstKey = firstKey;
    }

    public String getSecondKey() {
        return secondKey;
    }

    public void setSecondKey(String secondKey) {
        this.secondKey = secondKey;
    }
}
