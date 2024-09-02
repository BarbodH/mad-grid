package com.barbodh.madgrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private String id;
    private int score;
    private boolean playing;
}
