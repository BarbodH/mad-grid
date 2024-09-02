package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.R;
import com.barbodh.madgrid.model.IncomingPlayer;
import com.barbodh.madgrid.model.LobbyNotification;
import com.barbodh.madgrid.util.StringUtil;
import com.google.gson.Gson;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class LobbyActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    private StompClient stompClient;
    private Disposable disposableLifecycle;
    private Disposable disposableTopic;

    // TODO: This is a temporary solution; delete later.
    private final String[] modeStrings = {"Classic", "Reverse", "Messy"};

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        var gson = new Gson();
        var mode = getIntent().getIntExtra("mode", 0);
        var incomingPlayer = new IncomingPlayer(StringUtil.generateRandomString(10), mode);
        Log.d("LobbyActivity", "Generated ID: " + incomingPlayer.getId());

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://<SERVER_IP>:8080/ws");

        // Send player to the server lobby for matchmaking
        disposableLifecycle = stompClient.lifecycle().subscribe(lifecycleEvent -> {
            if (Objects.requireNonNull(lifecycleEvent.getType()) == LifecycleEvent.Type.OPENED) {
                stompClient.send("/game/seek-opponent", gson.toJson(incomingPlayer)).subscribe();
            }
        });

        // Wait for player to get matched by the server and receive game information
        disposableTopic = stompClient.topic("/player/" + incomingPlayer.getId() + "/lobby/notify")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    var lobbyNotification = gson.fromJson(topicMessage.getPayload(), LobbyNotification.class);
                    if (lobbyNotification.isMatched()) {
                        var intent = new Intent(this, GameActivity.class);
                        intent.putExtra("mode", modeStrings[mode]);
                        startActivity(intent);
                    }
                });

        stompClient.connect();
    }

    /**
     * Pauses the activity and disposes of active WebSocket subscriptions. This method is called
     * when the activity is no longer in the foreground, ensuring that all WebSocket connections and
     * subscriptions are properly disposed of to prevent memory leaks and unnecessary resource usage.
     */
    @Override
    protected void onPause() {
        super.onPause();
        disposableLifecycle.dispose();
        disposableTopic.dispose();
    }
}
