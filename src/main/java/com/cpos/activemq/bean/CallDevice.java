package com.cpos.activemq.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallDevice {
	public static int msgType = 18;
	final String cardParkId, deviceId, deviceType, callCarPark, callDevice, udpId, udpPort;
}
