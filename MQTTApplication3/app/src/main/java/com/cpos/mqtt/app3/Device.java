package com.cpos.mqtt.app3;

public class Device {
    final String username,password, cardParkId;
    final int deviceType, deviceId;

    public Device(String username, String password, String cardParkId, int deviceType, int deviceId) {
        this.username = username;
        this.password = password;
        this.cardParkId = cardParkId;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public static Device fromString(String txt) {
        String[] infos = txt.split(",");
        return new Device(infos[0], infos[1], infos[2],Integer.parseInt(infos[3]),Integer.parseInt(infos[4]));
    }

    public String toString() {
        return String.format("%s,%s,%s,%d,%d",username, password, cardParkId, deviceType, deviceId);
    }
}
