package com.cpos.activemq.struct;

import com.cpos.activemq.bean.CallDevice;
import com.cpos.activemq.session.ControlCenterSession;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SoundInfo {

	public SoundInfo(ControlCenterSession session) {
		this("" // Dummy
				, (short) 0 // Dummy
				, session.getUsername(), session.getDeviceId(), session.getDeviceType());
	}

	public SoundInfo(CallDevice callDevice) {
		this( // to
				callDevice.getIp(), // Dummy
				callDevice.getPort(), // Dummy
				callDevice.getCardParkId(), callDevice.getDeviceId(), callDevice.getDeviceType());
	}

	String ip;
	short port;
	String str_car_park_id;
	int device_id;
	int device_type;
}
