package com.barbodh.madgrid.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultiplayerGame implements Parcelable {
    private String id;
    private int gameMode;
    private Player player1;
    private Player player2;
    private boolean active;

    protected MultiplayerGame(Parcel in) {
        id = in.readString();
        gameMode = in.readInt();
        active = in.readByte() != 0;
    }

    public static final Creator<MultiplayerGame> CREATOR = new Creator<>() {
        @Override
        public MultiplayerGame createFromParcel(Parcel in) {
            return new MultiplayerGame(in);
        }

        @Override
        public MultiplayerGame[] newArray(int size) {
            return new MultiplayerGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(gameMode);
        parcel.writeByte((byte) (active ? 1 : 0));
    }
}
