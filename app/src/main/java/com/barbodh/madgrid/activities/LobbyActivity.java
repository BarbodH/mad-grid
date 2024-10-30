package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.MadGridApplication;
import com.barbodh.madgrid.R;
import com.barbodh.madgrid.model.IncomingPlayer;
import com.barbodh.madgrid.model.LobbyNotification;
import com.barbodh.madgrid.util.StringUtil;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.StompClient;

public class LobbyActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    private final Gson gson = new Gson();
    private Disposable disposableTopic1;
    private Disposable disposableTopic2;
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

        joinLobby();
    }

    /**
     *
     */
    private void joinLobby() {
        // STOMP handshake
        MadGridApplication.getInstance().initializeStompClient();
        stompClient = MadGridApplication.getInstance().getStompClient();

        // Send player to the server lobby for matchmaking
        var connectionSuccess = new AtomicBoolean(false);
        disposableTopic1 = stompClient.send("/game/seek-opponent", gson.toJson(incomingPlayer)).subscribe(
                () -> runOnUiThread(() -> {
                    connectionSuccess.set(true);
                    ((TextView) findViewById(R.id.lobby_text_message)).setText(getString(R.string.lobby_message_successful));
                    findViewById(R.id.lobby_button_retry).setVisibility(View.GONE);
                }),
                throwable -> runOnUiThread(() -> {
                    connectionSuccess.set(false);
                    ((TextView) findViewById(R.id.lobby_text_message)).setText(getString(R.string.lobby_message_error));
                    findViewById(R.id.lobby_button_retry).setVisibility(View.VISIBLE);
                })
        );
        if (!connectionSuccess.get()) return;

        // If connection is successful, wait for player to get matched by the server and receive game information
        disposableTopic2 = stompClient.topic("/player/" + incomingPlayer.getId() + "/lobby/notify").subscribe(topicMessage -> {
            var lobbyNotification = gson.fromJson(topicMessage.getPayload(), LobbyNotification.class);
            if (lobbyNotification.isMatched()) {
                var intent = new Intent(this, GameActivity.class);
                intent.putExtra("mode", modeStrings[incomingPlayer.getGameMode()]);
                intent.putExtra("type", 1);
                intent.putExtra("player_id", incomingPlayer.getId());
                intent.putExtra("multiplayer_game", lobbyNotification.getMultiplayerGame());
                startActivity(intent);
            }
        });
    }

    public void joinLobby(View view) {
        joinLobby();
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
        if (disposableTopic1 != null && !disposableTopic1.isDisposed()) {
            disposableTopic1.dispose();
        }
        if (disposableTopic2 != null && !disposableTopic2.isDisposed()) {
            disposableTopic2.dispose();
        }
    }
}
