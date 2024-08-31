package com.barbodh.madgrid.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.R;
import com.barbodh.madgrid.model.IncomingPlayer;
import com.google.gson.Gson;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class LobbyActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    private StompClient stompClient;

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        var incomingPlayer = new IncomingPlayer("123", 0);
        Gson gson = new Gson();

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://<SERVER_IP>:8080/ws");

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("LobbyActivity", "STOMP connection opened");
                    // Connection is established, now you can send your message
                    stompClient.send("/game/seek-opponent", gson.toJson(incomingPlayer))
                            .subscribe(() -> Log.d("LobbyActivity", "Message sent successfully"),
                                    throwable -> Log.e("LobbyActivity", "Error sending message", throwable));
                    break;
                case ERROR:
                    Log.e("LobbyActivity", "Error in STOMP connection", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("LobbyActivity", "STOMP connection closed");
                    break;
                case FAILED_SERVER_HEARTBEAT:
                    Log.e("LobbyActivity", "Failed server heartbeat");
                    break;
            }
        });

        stompClient.connect();
    }
}
