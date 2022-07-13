package com.cpos.mqtt.app3;

import java.util.HashMap;

public class Device {
    final String video_password,username,password, cardParkId, time,version,device_remark,video_id;
    final int deviceType, deviceId;

    public Device(HashMap<String, String> map) {
        this(
                map.get("video_password"),
                map.get("username"),
                map.get("password"),
                map.get("car_park_id"),
                map.get("time"),
                map.get("version"),
                map.get("device_remark"),
                map.get("video_id"),
                Integer.parseInt(map.get("device_type")),
                Integer.parseInt(map.get("device_id"))
        );
    }

    public Device(String video_password, String username, String password, String cardParkId, String time, String version, String device_remark, String video_id, int deviceType, int deviceId) {
        this.video_password = video_password;
        this.username = username;
        this.password = password;
        this.cardParkId = cardParkId;
        this.time = time;
        this.version = version;
        this.device_remark = device_remark;
        this.video_id = video_id;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public String display() {
        return String.format("Car Park ID: %s - Device Type: %d, Device ID: %d",cardParkId, deviceType, deviceId);
    }

    public String toString() {
        return String.format("%s,%s,%s,%d,%d",username, password, cardParkId, deviceType, deviceId);
    }
}
