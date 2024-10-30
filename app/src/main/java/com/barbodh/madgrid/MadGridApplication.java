package com.barbodh.madgrid;

import android.app.Application;

import lombok.Getter;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

@Getter
public class MadGridApplication extends Application {
    @Getter
    private static MadGridApplication instance;
    private StompClient stompClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void initializeStompClient() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://mad-grid-api.nn.r.appspot.com/ws");
        stompClient.connect();
    }
}
