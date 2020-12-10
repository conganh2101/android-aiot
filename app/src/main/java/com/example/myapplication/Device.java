package com.example.myapplication;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Device implements Serializable {
    private int DeviceId;
    private Room room;
    private String description;

    public Device() {
    }

    public Device(int deviceId, Room room, String description) {
        DeviceId = deviceId;
        this.room = room;
        this.description = description;
    }

    public Device(Room room, String description) {
        this.room = room;
        this.description = description;
    }

    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
