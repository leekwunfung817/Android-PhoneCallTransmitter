package com.cpos.activemq.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallDevice {
	final int msgType;
	final String cardParkId;
	final int deviceId, deviceType, callCarPark, callDevice;
	final String ip;
	final short port;
}
