package com.barbodh.madgrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameUpdate {
    private String gameId;
    private String playerId;
    private boolean result;
}
