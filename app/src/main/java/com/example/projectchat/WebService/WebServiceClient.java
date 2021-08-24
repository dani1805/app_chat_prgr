package com.example.projectchat.WebService;

import com.example.projectchat.Models.MyNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface WebServiceClient { //Interfaz donde se guardan los metodos relacionados con el Retrofit

    String BASE_URL = "https://fcm.googleapis.com/";
    String SERVER_TOKEN = "AAAAlFgpLl0:APA91bGTvG2pPFZOvJY7LCXq6Lgxr_MYWDHNKxFx3DlV6YJd79OvGgXgrrGwR78TMg09lTAeRLj_irKmPbH4e0h8r4lidQk6Zio4mtSGG5sg196TnJxBK5U6XRPVlP9-wGqphpmY5E1-";

    @Headers({"Authorization:key=" + SERVER_TOKEN, "Content-Type:application/json"})
    @POST("fcm/send")
    Call<Object> sendNotification(@Body MyNotification mynotif);
}

