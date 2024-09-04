package com.barbodh.madgrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerHeartbeat {
    private String gameId;
    private String playerId;
}
