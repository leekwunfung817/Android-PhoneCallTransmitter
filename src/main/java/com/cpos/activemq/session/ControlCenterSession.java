package com.cpos.activemq.session;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cpos.activemq.bean.CallDevice;
import com.cpos.activemq.task.TaskBase;
import com.cpos.net.MqttInternet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ControlCenterSession {

	static HashMap<String, ControlCenterSession> CONTROL_CENTER_SESSION_MAP = new HashMap<>();

	final String username, password;
	final int deviceId;
	final String deviceName;
	final int deviceType = 6;
//	public AtomicBoolean isRequesting = new AtomicBoolean(false);
	public CallDevice callingDevice = null;
	public TaskBase requestingTaskBase = null;
	
	public ControlCenterSession(String username, String password, int deviceId, String deviceName) {
		super();
		this.username = username;
		this.password = password;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}

	public static ControlCenterSession create(String username, String password, int deviceId, String deviceName) {
		ControlCenterSession controlCenterSession = new ControlCenterSession(username, password, deviceId, deviceName);
		return controlCenterSession;
	}

	public static ControlCenterSession get(String deviceId) {
		return CONTROL_CENTER_SESSION_MAP.get(deviceId);
	}

	public static ControlCenterSession distroy(String deviceId) {
		return CONTROL_CENTER_SESSION_MAP.remove(deviceId);
	}

	public void startCall(CallDevice callingDevice) {
		this.callingDevice = callingDevice;
	}

	public void endCall() {
		this.callingDevice = null;
	}

}
