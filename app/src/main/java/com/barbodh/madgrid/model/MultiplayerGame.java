package com.barbodh.madgrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultiplayerGame {
    private String id;
    private int gameMode;
    private Player player1;
    private Player player2;
    private boolean active;
}
