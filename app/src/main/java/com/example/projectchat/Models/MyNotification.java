package com.example.projectchat.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyNotification { // Clase para guardar y enviar las notif

    @SerializedName("registration_ids")
    List<String> tokens;
    NotificationData data;

    public MyNotification(List<String> tokens, NotificationData data) {
        this.tokens = tokens;
        this.data = data;
    }

    public MyNotification() {

    }


}
