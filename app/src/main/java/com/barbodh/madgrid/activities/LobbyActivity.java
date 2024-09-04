package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.MadGridApplication;
import com.barbodh.madgrid.R;
import com.barbodh.madgrid.model.IncomingPlayer;
import com.barbodh.madgrid.model.LobbyNotification;
import com.barbodh.madgrid.util.StringUtil;
import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.StompClient;

public class LobbyActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    private final Gson gson = new Gson();
    private Disposable disposableTopic;
    private StompClient stompClient;
    private IncomingPlayer incomingPlayer;

    // TODO: This is a temporary solution; delete later.
    private final String[] modeStrings = {"Classic", "Reverse", "Messy"};

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        var mode = getIntent().getIntExtra("mode", 0);
        incomingPlayer = new IncomingPlayer(StringUtil.generateRandomString(10), mode);
        Log.d("LobbyActivity", "Generated ID: " + incomingPlayer.getId());

        // STOMP handshake
        // TODO: This is a temporary solution; future solutions should monitor connectivity and re-use previous handshakes, if feasible.
        MadGridApplication.getInstance().initializeStompClient();
        stompClient = MadGridApplication.getInstance().getStompClient();

        // Send player to the server lobby for matchmaking
        stompClient.send("/game/seek-opponent", gson.toJson(incomingPlayer)).subscribe();

        // Wait for player to get matched by the server and receive game information
        disposableTopic = stompClient.topic("/player/" + incomingPlayer.getId() + "/lobby/notify").subscribe(topicMessage -> {
            var lobbyNotification = gson.fromJson(topicMessage.getPayload(), LobbyNotification.class);
            if (lobbyNotification.isMatched()) {
                var intent = new Intent(this, GameActivity.class);
                intent.putExtra("mode", modeStrings[mode]);
                intent.putExtra("type", 1);
                intent.putExtra("player_id", incomingPlayer.getId());
                intent.putExtra("multiplayer_game", lobbyNotification.getMultiplayerGame());
                startActivity(intent);
            }
        });
    }

    public void returnHome(View view) {
        stompClient.send("/game/exit-lobby", gson.toJson(incomingPlayer.getId())).subscribe();
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Called when the activity is no longer visible to the user. Disposes of the `disposableTopic`
     * to release resources and prevent memory leaks.
     */
    @Override
    protected void onStop() {
        super.onStop();
        stompClient.send("/game/exit-lobby", incomingPlayer.getId());
        if (disposableTopic != null && !disposableTopic.isDisposed()) {
            disposableTopic.dispose();
        }
    }
}
