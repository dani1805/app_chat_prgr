package com.example.projectchat.Models;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projectchat.MainActivity;
import com.example.projectchat.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.nio.channels.Channel;
import java.util.Date;
import java.util.Map;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.CHANNEL;

public class Push extends FirebaseMessagingService { // Clase para habilitar las notificaciones FCM

    private static final String TAG = "myFirebaseMessagingService";
    private static final String CHANNEL_ID = "3";
    private static final int notificationId = 5;

    private CollectionReference topicRef;
    private FirebaseFirestore db;

    @Override
    public void onNewToken(@NonNull String s)  {
        super.onNewToken(s);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        Log.d(TAG,"From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("push", "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            String body = data.get("body");
            String title = data.get("title");

            sendNotificacion(body, title);
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("push", "Message data payload: " + remoteMessage.getData());
        }
    }

    private void sendNotificacion(String body, String title) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
    }


}


