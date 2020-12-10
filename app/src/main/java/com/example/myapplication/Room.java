package com.example.myapplication;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
    private int RoomId;
    private String description;

    public Room() {
    }

    public Room( String description) {
        this.description = description;
    }

    public Room(int roomId, String description) {
        RoomId = roomId;
        this.description = description;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int roomId) {
        RoomId = roomId;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return this.description;
    }
}
