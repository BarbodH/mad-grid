package com.barbodh.madgrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LobbyNotification {
    private MultiplayerGame multiplayerGame;
    private boolean matched;
}
