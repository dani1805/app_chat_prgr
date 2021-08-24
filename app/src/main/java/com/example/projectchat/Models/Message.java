package com.example.projectchat.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Timestamp;
import java.util.Date;

public class Message { // Clase de los mensajes del foro

    String name;
    @ServerTimestamp // Formatea el tipo Date a TimeStamp
    Date date;
    String message;

    public Message(String name, Date date, String message) {
        this.name = name;
        this.date = date;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message() {

    }


}
