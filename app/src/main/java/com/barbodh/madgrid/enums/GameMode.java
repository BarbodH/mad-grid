package com.barbodh.madgrid.enums;

public enum GameMode {

    CLASSIC("Classic"),
    REVERSE("Reverse"),
    MESSY("Messy");

    private final String value;

    GameMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
